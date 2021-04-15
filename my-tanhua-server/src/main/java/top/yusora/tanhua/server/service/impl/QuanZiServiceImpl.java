package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import org.springframework.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.dubbo.server.api.QuanZiApi;
import top.yusora.tanhua.dubbo.server.api.VisitorsApi;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Comment;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Publish;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Visitors;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.QuanZiService;
import top.yusora.tanhua.server.service.QuanziMQService;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.service.VideoMQService;
import top.yusora.tanhua.server.vo.CommentVo;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.QuanZiVo;
import top.yusora.tanhua.server.vo.VisitorsVo;
import top.yusora.tanhua.service.PicUploadService;
import top.yusora.tanhua.utils.RelativeDateFormat;
import top.yusora.tanhua.utils.UserThreadLocal;
import top.yusora.tanhua.vo.PicUploadResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author heyu
 */
@Service
@Slf4j
public class QuanZiServiceImpl implements QuanZiService {

    @DubboReference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @DubboReference(version = "1.0.0")
    private VisitorsApi visitorsApi;


    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PicUploadService picUploadService;

    @Autowired
    private QuanziMQService quanziMQService;

    @Autowired
    private VideoMQService videoMQService;


    /**
     * 查询好友动态
     *
     * @param page     当前页数
     * @param pageSize 每页显示条数
     * @param token Jwt Token
     * @return PageResult 圈子结果集
     */
    @Override
    public PageResult queryPublishList(Integer page, Integer pageSize, String token) {
        //分析：通过dubbo中的服务查询用户的好友动态
        //通过mysql查询用户的信息，回写到结果对象中（QuanZiVo）
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //获取User对象，无需对User对象校验，其一定不为null
        Long userId = UserThreadLocal.get();

        //---------------------Dubbo查询圈子数据-----------------------
        //RPC调用必须防止NPE
        Optional<PageInfo<Publish>> pageInfo = Optional.ofNullable(this.quanZiApi.queryPublishList(userId, page, pageSize));
        //拿到圈子动态发布列表
        List<Publish> records = pageInfo.map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            //无数据直接返回
            return pageResult;
        }
        List<QuanZiVo> quanZiVoList = fillQuanZiVos(records);

        //放入pageResult, 返回
        pageResult.setItems(quanZiVoList);
        return pageResult;
    }


    /**
     * 发布动态
     * @param textContent 文字内容
     * @Nullable @param location 位置
     * @Nullable @param latitude 纬度
     * @Nullable @param longitude 经度
     * @Nullable @param multipartFile 图片文件
     * @return PublishId
     */
    @Override
    public String savePublish(String textContent, @Nullable String location, @Nullable String latitude, @Nullable String longitude,
                              @Nullable MultipartFile[] multipartFile) {
        //校验当前用户上下文
        Long userId = UserThreadLocal.get();

        //根据传参构建
        Publish publish = new Publish();
        publish.setUserId(userId);
        publish.setText(textContent);
        //----------以下为非必要参数 @Nullable----------
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);
        //--------------------------------------------
        publish.setSeeType(1);

        //图片上传
        List<String> picUrls = new ArrayList<>();
        Optional<MultipartFile[]> multipartFileOpt = Optional.ofNullable(multipartFile);

        //如果有照片上传
        multipartFileOpt.ifPresent(multipartFiles -> {
            for (MultipartFile file : multipartFiles) {
                PicUploadResult picUploadResult = this.picUploadService.upload(file);
                picUrls.add(picUploadResult.getName());
            }
        });

        publish.setMedias(picUrls);

        String publishId = this.quanZiApi.savePublish(publish);
        //发送消息
        this.quanziMQService.publishMsg(publishId);
        //RPC调用 发布动态
        return publishId;
    }





    /**
     * 查询推荐动态
     *
     * @param page     当前页数
     * @param pageSize 每页显示条数
     * @return 推荐动态结果
     */
    @Override
    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {
        // 初始化PageResult
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //RPC查询圈子数据 注意远程调用NPE处理
        Optional<PageInfo<Publish>> pageInfo = Optional.ofNullable(this.quanZiApi
                .queryRecommendPublishList(userId,page,pageSize));

        List<Publish> records = pageInfo.map(PageInfo::getRecords).orElse(null);

        if (CollUtil.isEmpty(records)) {
            //无数据
            return pageResult;
        }

        pageResult.setItems(this.fillQuanZiVos(records));
        return pageResult;
    }




    /**
     * 点赞
     *
     * @param publishId 动态ID
     * @return 业务成功: 点赞数  业务失败：null
     */
    @Override
    public Long likeComment(String publishId, QuanZiType type) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        //Rpc进行点赞,远程调用需要NPE处理
        Boolean result = Optional.ofNullable(this.quanZiApi.likeComment(userId, publishId,type))
                .orElse(false);

        //模拟异常
        //return null;

        if(result){
            switch (type){
                case PUBLISH:
                    //发送消息
                    this.quanziMQService.likePublishMsg(publishId);
                    break;
                case VIDEO:
                    //发送消息
                    this.videoMQService.likeVideoMsg(publishId);
                    break;
                default:
                    break;
            }

            //查询点赞数
            return this.quanZiApi.queryLikeCount(publishId);
        }

        return null;
    }

    /**
     * 取消点赞
     *
     * @param publishId 动态ID
     * @return 业务是否成功
     */
    @Override
    public Long disLikeComment(String publishId,QuanZiType type) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        //Rpc进行取消点赞,远程调用需要NPE处理
        Boolean result = Optional.ofNullable(this.quanZiApi.disLikeComment(userId, publishId))
                .orElse(false);

        if(result){
            switch (type){
                case PUBLISH:
                    //发送消息
                    this.quanziMQService.disLikePublishMsg(publishId);
                    break;
                case VIDEO:
                    //发送消息
                    this.videoMQService.disLikeVideoMsg(publishId);
                    break;
                default:
                    break;
            }

            //查询点赞数
            return this.quanZiApi.queryLikeCount(publishId);
        }

        return null;

    }

    /**
     * 喜欢
     *
     * @param publishId 动态ID
     * @return 业务是否成功 业务成功: 喜欢数  业务失败：null
     */
    @Override
    public Long loveComment(String publishId,QuanZiType type) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        //Rpc进行喜欢,远程调用需要NPE处理
        Boolean result = Optional.ofNullable(this.quanZiApi.loveComment(userId, publishId,type))
                .orElse(false);

        if(result){
            //发送消息
            this.quanziMQService.lovePublishMsg(publishId);
            //查询点赞数
            return this.quanZiApi.queryLoveCount(publishId);
        }

        return null;
    }

    /**
     * 取消喜欢
     *
     * @param publishId 动态ID
     * @return 业务是否成功 业务成功: 喜欢数  业务失败：null
     */
    @Override
    public Long disLoveComment(String publishId) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        //Rpc进行取消喜欢,远程调用需要NPE处理
        Boolean result = Optional.ofNullable(this.quanZiApi.disLoveComment(userId, publishId))
                .orElse(false);

        if(result){
            //发送消息
            this.quanziMQService.disLovePublishMsg(publishId);
            //查询点赞数
            return this.quanZiApi.queryLoveCount(publishId);
        }

        return null;

    }

    /**
     * 查询单条动态信息
     * 用户点击评论时需要查询单条动态详情
     *
     * @param publishId 动态id
     * @return QuanziVo 动态数据
     */
    @Override
    public QuanZiVo queryById(String publishId) {
        //发送消息
        this.quanziMQService.queryPublishMsg(publishId);
        //Rpc进行查询,远程调用需要NPE处理
        //将publish -> QuanZiVo
        return Optional.ofNullable(this.quanZiApi.queryPublishById(publishId))
                .map(Arrays::asList).map(this::fillQuanZiVos).map(list -> list.get(0))
                .orElse(null);
    }

    /**
     * 分页查询评论列表
     *
     * @param publishId 父动态id
     * @param page      当前页数
     * @param pageSize  每页条数
     * @return 分页结果列表
     */
    @Override
    public PageResult queryCommentList(String publishId, Integer page, Integer pageSize) {
        // 初始化PageResult对象
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);

        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //RPC调用查询评论列表数据
        Optional<PageInfo<Comment>> pageInfo = Optional.ofNullable(this.quanZiApi.queryCommentList(publishId, page, pageSize));
        List<Comment> records = pageInfo.map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            //数据不存在直接返回
            return pageResult;
        }

        //查询用户信息
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"));

        //------------------转换成CommentVo对象------------------
        List<CommentVo> commentVoList = new ArrayList<>();


        records.forEach(record -> {
            //---------------填充评论数据---------------------
            CommentVo commentVo = new CommentVo();
            commentVo.setContent(record.getContent());
            commentVo.setId(record.getId().toHexString());
            commentVo.setCreateDate(DateUtil.format(new Date(record.getCreated()), "HH:mm"));
            //是否点赞
            commentVo.setHasLiked(this.quanZiApi.queryUserIsLike(userId, commentVo.getId()) ? 1 : 0);
            //点赞数
            commentVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(commentVo.getId())));

            //--------------填充用户基本信息-------------------
            UserInfo userInfo = userInfoMap.get(record.getUserId());
            //头像
            commentVo.setAvatar(userInfo.getLogo());
            //昵称
            commentVo.setNickname(userInfo.getNickName());

            commentVoList.add(commentVo);
        });

        //放入分页结果，返回
        pageResult.setItems(commentVoList);
        return pageResult;
    }

    /**
     * 保存评论
     *
     * @param publishId 父动态id
     * @param content   评论内容
     * @return 业务是否成功
     */
    @Override
    public Boolean saveComments(String publishId, String content) {

        //获得用户上下文
        Long userId = UserThreadLocal.get();

        Boolean result = Optional.ofNullable(this.quanZiApi.saveComment(userId, publishId, content, QuanZiType.PUBLISH))
                .orElse(false);
        if(result){
            //发送消息
            this.quanziMQService.commentPublishMsg(publishId);
        }
        return result;
    }

    /**
     * 指定用户的所有动态
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 动态列表
     */
    @Override
    public PageResult queryAlbumList(Long userId, Integer page, Integer pageSize) {
        //init
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //查询所有动态数据
        List<Publish> records = Optional.ofNullable(this.quanZiApi.queryAlbumList(userId, page, pageSize))
                .map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        //查询用户基本信息并转换成圈子Vo对象
        List<QuanZiVo> quanZiVoList = this.fillQuanZiVos(records);

        pageResult.setItems(quanZiVoList);
        return pageResult;
    }

    /**
     * 谁看过我
     *
     * @return 访客数据列表 @Nonnull
     */
    @Override
    public List<VisitorsVo> queryVisitorsList() {
        //获取用户上下文
        Long userId = UserThreadLocal.get();

        List<Visitors> visitorsList = this.visitorsApi.queryMyVisitor(userId);

        if (CollUtil.isEmpty(visitorsList)) {
            return Collections.emptyList();
        }

        //获取所有访客Id
        List<Object> userIds = CollUtil.getFieldValues(visitorsList, "visitorUserId");

        //获取用户信息字典
        Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(userIds);

        List<VisitorsVo> visitorsVoList = new ArrayList<>();

        visitorsList.forEach(visitors -> {
            UserInfo userInfo = userInfoMap.get(visitors.getVisitorUserId());
            VisitorsVo visitorsVo = BeanUtil.toBeanIgnoreError(userInfo, VisitorsVo.class);
            visitorsVo.setGender(userInfo.getSex().name().toLowerCase());
            visitorsVo.setFateValue(visitors.getScore().intValue());
            visitorsVoList.add(visitorsVo);
        });

        return visitorsVoList;
    }


    /**
     * 填充用户信息（单个QuanziVo）
     *
     * @param userInfo 用户基本信息
     * @param quanZiVo 圈子Vo对象
     */
    private void fillUserInfoToQuanZiVo(UserInfo userInfo, QuanZiVo quanZiVo){

        //hutool工具类 复制属性 第三个参数为忽略的属性名
        BeanUtil.copyProperties(userInfo, quanZiVo, "id");


        quanZiVo.setGender(userInfo.getSex().name().toLowerCase());
        quanZiVo.setTags(StringUtils.split(userInfo.getTags(), ','));

        //当前用户
        Long userId = UserThreadLocal.get();

        //评论数
        quanZiVo.setCommentCount(Convert.toInt(this.quanZiApi.queryCommentCount(quanZiVo.getId())));
        //TODO 距离
        quanZiVo.setDistance("1.2公里");
        // 是否点赞（1是，0否）
        quanZiVo.setHasLiked(this.quanZiApi.queryUserIsLike(userId, quanZiVo.getId()) ? 1 : 0);
        // 点赞数
        quanZiVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(quanZiVo.getId())));
        // 是否喜欢（1是，0否）
        quanZiVo.setHasLoved(this.quanZiApi.queryUserIsLove(userId, quanZiVo.getId()) ? 1 : 0);
        // 喜欢数
        quanZiVo.setLoveCount(Convert.toInt(this.quanZiApi.queryLoveCount(quanZiVo.getId())));
    }

    /**
     * 填充圈子数据 以及 发布用户信息（QuanziVo 列表）
     * @param records 圈子动态发布列表
     * @return 填充信息后的QuanziVo 列表
     */
    private List<QuanZiVo> fillQuanZiVos(List<Publish> records) {

        //获取用户信息Map
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"));

        //------------------------数据填充-------------------------
        List<QuanZiVo> quanZiVoList = new ArrayList<>();
        records.forEach(publish -> {
            //-----------------------将圈子数据填充到Vo对象中--------------------
            QuanZiVo quanZiVo = new QuanZiVo();
            quanZiVo.setId(publish.getId().toHexString());
            quanZiVo.setTextContent(publish.getText());
            quanZiVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            quanZiVo.setUserId(publish.getUserId());
            quanZiVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));

            //-----------------------将用户基本信息填充到Vo对象中--------------------
            UserInfo userInfo = userInfoMap.get(quanZiVo.getUserId());
            this.fillUserInfoToQuanZiVo(userInfo, quanZiVo);
            //加入列表
            quanZiVoList.add(quanZiVo);
        });


        return quanZiVoList;
    }




}

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
     * ??????????????????
     *
     * @param page     ????????????
     * @param pageSize ??????????????????
     * @param token Jwt Token
     * @return PageResult ???????????????
     */
    @Override
    public PageResult queryPublishList(Integer page, Integer pageSize, String token) {
        //???????????????dubbo???????????????????????????????????????
        //??????mysql???????????????????????????????????????????????????QuanZiVo???
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //??????User??????????????????User??????????????????????????????null
        Long userId = UserThreadLocal.get();

        //---------------------Dubbo??????????????????-----------------------
        //RPC??????????????????NPE
        Optional<PageInfo<Publish>> pageInfo = Optional.ofNullable(this.quanZiApi.queryPublishList(userId, page, pageSize));
        //??????????????????????????????
        List<Publish> records = pageInfo.map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            //?????????????????????
            return pageResult;
        }
        List<QuanZiVo> quanZiVoList = fillQuanZiVos(records);

        //??????pageResult, ??????
        pageResult.setItems(quanZiVoList);
        return pageResult;
    }


    /**
     * ????????????
     * @param textContent ????????????
     * @Nullable @param location ??????
     * @Nullable @param latitude ??????
     * @Nullable @param longitude ??????
     * @Nullable @param multipartFile ????????????
     * @return PublishId
     */
    @Override
    public String savePublish(String textContent, @Nullable String location, @Nullable String latitude, @Nullable String longitude,
                              @Nullable MultipartFile[] multipartFile) {
        //???????????????????????????
        Long userId = UserThreadLocal.get();

        //??????????????????
        Publish publish = new Publish();
        publish.setUserId(userId);
        publish.setText(textContent);
        //----------???????????????????????? @Nullable----------
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);
        //--------------------------------------------
        publish.setSeeType(1);

        //????????????
        List<String> picUrls = new ArrayList<>();
        Optional<MultipartFile[]> multipartFileOpt = Optional.ofNullable(multipartFile);

        //?????????????????????
        multipartFileOpt.ifPresent(multipartFiles -> {
            for (MultipartFile file : multipartFiles) {
                PicUploadResult picUploadResult = this.picUploadService.upload(file);
                picUrls.add(picUploadResult.getName());
            }
        });

        publish.setMedias(picUrls);

        String publishId = this.quanZiApi.savePublish(publish);
        //????????????
        this.quanziMQService.publishMsg(publishId);
        //RPC?????? ????????????
        return publishId;
    }





    /**
     * ??????????????????
     *
     * @param page     ????????????
     * @param pageSize ??????????????????
     * @return ??????????????????
     */
    @Override
    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {
        // ?????????PageResult
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //?????????????????????
        Long userId = UserThreadLocal.get();

        //RPC?????????????????? ??????????????????NPE??????
        Optional<PageInfo<Publish>> pageInfo = Optional.ofNullable(this.quanZiApi
                .queryRecommendPublishList(userId,page,pageSize));

        List<Publish> records = pageInfo.map(PageInfo::getRecords).orElse(null);

        if (CollUtil.isEmpty(records)) {
            //?????????
            return pageResult;
        }

        pageResult.setItems(this.fillQuanZiVos(records));
        return pageResult;
    }




    /**
     * ??????
     *
     * @param publishId ??????ID
     * @return ????????????: ?????????  ???????????????null
     */
    @Override
    public Long likeComment(String publishId, QuanZiType type) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        //Rpc????????????,??????????????????NPE??????
        Boolean result = Optional.ofNullable(this.quanZiApi.likeComment(userId, publishId,type))
                .orElse(false);

        //????????????
        //return null;

        if(result){
            switch (type){
                case PUBLISH:
                    //????????????
                    this.quanziMQService.likePublishMsg(publishId);
                    break;
                case VIDEO:
                    //????????????
                    this.videoMQService.likeVideoMsg(publishId);
                    break;
                default:
                    break;
            }

            //???????????????
            return this.quanZiApi.queryLikeCount(publishId);
        }

        return null;
    }

    /**
     * ????????????
     *
     * @param publishId ??????ID
     * @return ??????????????????
     */
    @Override
    public Long disLikeComment(String publishId,QuanZiType type) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        //Rpc??????????????????,??????????????????NPE??????
        Boolean result = Optional.ofNullable(this.quanZiApi.disLikeComment(userId, publishId))
                .orElse(false);

        if(result){
            switch (type){
                case PUBLISH:
                    //????????????
                    this.quanziMQService.disLikePublishMsg(publishId);
                    break;
                case VIDEO:
                    //????????????
                    this.videoMQService.disLikeVideoMsg(publishId);
                    break;
                default:
                    break;
            }

            //???????????????
            return this.quanZiApi.queryLikeCount(publishId);
        }

        return null;

    }

    /**
     * ??????
     *
     * @param publishId ??????ID
     * @return ?????????????????? ????????????: ?????????  ???????????????null
     */
    @Override
    public Long loveComment(String publishId,QuanZiType type) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        //Rpc????????????,??????????????????NPE??????
        Boolean result = Optional.ofNullable(this.quanZiApi.loveComment(userId, publishId,type))
                .orElse(false);

        if(result){
            //????????????
            this.quanziMQService.lovePublishMsg(publishId);
            //???????????????
            return this.quanZiApi.queryLoveCount(publishId);
        }

        return null;
    }

    /**
     * ????????????
     *
     * @param publishId ??????ID
     * @return ?????????????????? ????????????: ?????????  ???????????????null
     */
    @Override
    public Long disLoveComment(String publishId) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        //Rpc??????????????????,??????????????????NPE??????
        Boolean result = Optional.ofNullable(this.quanZiApi.disLoveComment(userId, publishId))
                .orElse(false);

        if(result){
            //????????????
            this.quanziMQService.disLovePublishMsg(publishId);
            //???????????????
            return this.quanZiApi.queryLoveCount(publishId);
        }

        return null;

    }

    /**
     * ????????????????????????
     * ???????????????????????????????????????????????????
     *
     * @param publishId ??????id
     * @return QuanziVo ????????????
     */
    @Override
    public QuanZiVo queryById(String publishId) {
        //????????????
        this.quanziMQService.queryPublishMsg(publishId);
        //Rpc????????????,??????????????????NPE??????
        //???publish -> QuanZiVo
        return Optional.ofNullable(this.quanZiApi.queryPublishById(publishId))
                .map(Arrays::asList).map(this::fillQuanZiVos).map(list -> list.get(0))
                .orElse(null);
    }

    /**
     * ????????????????????????
     *
     * @param publishId ?????????id
     * @param page      ????????????
     * @param pageSize  ????????????
     * @return ??????????????????
     */
    @Override
    public PageResult queryCommentList(String publishId, Integer page, Integer pageSize) {
        // ?????????PageResult??????
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);

        //?????????????????????
        Long userId = UserThreadLocal.get();

        //RPC??????????????????????????????
        Optional<PageInfo<Comment>> pageInfo = Optional.ofNullable(this.quanZiApi.queryCommentList(publishId, page, pageSize));
        List<Comment> records = pageInfo.map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            //???????????????????????????
            return pageResult;
        }

        //??????????????????
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"));

        //------------------?????????CommentVo??????------------------
        List<CommentVo> commentVoList = new ArrayList<>();


        records.forEach(record -> {
            //---------------??????????????????---------------------
            CommentVo commentVo = new CommentVo();
            commentVo.setContent(record.getContent());
            commentVo.setId(record.getId().toHexString());
            commentVo.setCreateDate(DateUtil.format(new Date(record.getCreated()), "HH:mm"));
            //????????????
            commentVo.setHasLiked(this.quanZiApi.queryUserIsLike(userId, commentVo.getId()) ? 1 : 0);
            //?????????
            commentVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(commentVo.getId())));

            //--------------????????????????????????-------------------
            UserInfo userInfo = userInfoMap.get(record.getUserId());
            //??????
            commentVo.setAvatar(userInfo.getLogo());
            //??????
            commentVo.setNickname(userInfo.getNickName());

            commentVoList.add(commentVo);
        });

        //???????????????????????????
        pageResult.setItems(commentVoList);
        return pageResult;
    }

    /**
     * ????????????
     *
     * @param publishId ?????????id
     * @param content   ????????????
     * @return ??????????????????
     */
    @Override
    public Boolean saveComments(String publishId, String content) {

        //?????????????????????
        Long userId = UserThreadLocal.get();

        Boolean result = Optional.ofNullable(this.quanZiApi.saveComment(userId, publishId, content, QuanZiType.PUBLISH))
                .orElse(false);
        if(result){
            //????????????
            this.quanziMQService.commentPublishMsg(publishId);
        }
        return result;
    }

    /**
     * ???????????????????????????
     *
     * @param userId   ??????Id
     * @param page     ??????
     * @param pageSize ????????????
     * @return ????????????
     */
    @Override
    public PageResult queryAlbumList(Long userId, Integer page, Integer pageSize) {
        //init
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //????????????????????????
        List<Publish> records = Optional.ofNullable(this.quanZiApi.queryAlbumList(userId, page, pageSize))
                .map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        //??????????????????????????????????????????Vo??????
        List<QuanZiVo> quanZiVoList = this.fillQuanZiVos(records);

        pageResult.setItems(quanZiVoList);
        return pageResult;
    }

    /**
     * ????????????
     *
     * @return ?????????????????? @Nonnull
     */
    @Override
    public List<VisitorsVo> queryVisitorsList() {
        //?????????????????????
        Long userId = UserThreadLocal.get();

        List<Visitors> visitorsList = this.visitorsApi.queryMyVisitor(userId);

        if (CollUtil.isEmpty(visitorsList)) {
            return Collections.emptyList();
        }

        //??????????????????Id
        List<Object> userIds = CollUtil.getFieldValues(visitorsList, "visitorUserId");

        //????????????????????????
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
     * ???????????????????????????QuanziVo???
     *
     * @param userInfo ??????????????????
     * @param quanZiVo ??????Vo??????
     */
    private void fillUserInfoToQuanZiVo(UserInfo userInfo, QuanZiVo quanZiVo){

        //hutool????????? ???????????? ????????????????????????????????????
        BeanUtil.copyProperties(userInfo, quanZiVo, "id");


        quanZiVo.setGender(userInfo.getSex().name().toLowerCase());
        quanZiVo.setTags(StringUtils.split(userInfo.getTags(), ','));

        //????????????
        Long userId = UserThreadLocal.get();

        //?????????
        quanZiVo.setCommentCount(Convert.toInt(this.quanZiApi.queryCommentCount(quanZiVo.getId())));
        //TODO ??????
        quanZiVo.setDistance("1.2??????");
        // ???????????????1??????0??????
        quanZiVo.setHasLiked(this.quanZiApi.queryUserIsLike(userId, quanZiVo.getId()) ? 1 : 0);
        // ?????????
        quanZiVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(quanZiVo.getId())));
        // ???????????????1??????0??????
        quanZiVo.setHasLoved(this.quanZiApi.queryUserIsLove(userId, quanZiVo.getId()) ? 1 : 0);
        // ?????????
        quanZiVo.setLoveCount(Convert.toInt(this.quanZiApi.queryLoveCount(quanZiVo.getId())));
    }

    /**
     * ?????????????????? ?????? ?????????????????????QuanziVo ?????????
     * @param records ????????????????????????
     * @return ??????????????????QuanziVo ??????
     */
    private List<QuanZiVo> fillQuanZiVos(List<Publish> records) {

        //??????????????????Map
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"));

        //------------------------????????????-------------------------
        List<QuanZiVo> quanZiVoList = new ArrayList<>();
        records.forEach(publish -> {
            //-----------------------????????????????????????Vo?????????--------------------
            QuanZiVo quanZiVo = new QuanZiVo();
            quanZiVo.setId(publish.getId().toHexString());
            quanZiVo.setTextContent(publish.getText());
            quanZiVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            quanZiVo.setUserId(publish.getUserId());
            quanZiVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));

            //-----------------------??????????????????????????????Vo?????????--------------------
            UserInfo userInfo = userInfoMap.get(quanZiVo.getUserId());
            this.fillUserInfoToQuanZiVo(userInfo, quanZiVo);
            //????????????
            quanZiVoList.add(quanZiVo);
        });


        return quanZiVoList;
    }




}

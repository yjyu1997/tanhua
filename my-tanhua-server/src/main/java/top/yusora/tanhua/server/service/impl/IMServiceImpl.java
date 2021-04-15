package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.AnnouncementApi;
import top.yusora.tanhua.dubbo.server.api.HuanXinApi;
import top.yusora.tanhua.dubbo.server.api.QuanZiApi;
import top.yusora.tanhua.dubbo.server.api.UsersApi;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Announcement;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Comment;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Users;
import top.yusora.tanhua.server.service.IMService;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.vo.*;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author heyu
 */
@Slf4j
@Service
public class IMServiceImpl implements IMService {

    @DubboReference(version = "1.0.0")
    private HuanXinApi huanXinApi;

    @DubboReference(version = "1.0.0")
    private UsersApi usersApi;

    @DubboReference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @DubboReference(version = "1.0.0")
    private AnnouncementApi announcementApi;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 根据环信用户名检索用户信息
     *
     * @param userName 环信用户名
     * @return 用户基本信息
     */
    @Override
    public UserInfoVo queryUserInfoByUserName(String userName) {
        UserInfoVo result = Optional.ofNullable(this.huanXinApi.queryUserByUserName(userName))
                .map(huanXinUser -> this.userInfoService.queryUserInfoByUserId(huanXinUser.getUserId()))
                .map(userInfo -> {
                    UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class, "marriage");
                    userInfoVo.setGender(userInfo.getSex().name().toLowerCase());
                    userInfoVo.setMarriage(StrUtil.equals("已婚", userInfo.getMarriage()) ? 1 : 0);
                    return userInfoVo;
                }).orElse(null);
        log.info("环信用户信息为：{}",result);
        return result;
    }

    /**
     * 添加好友
     *
     * @param friendId 好友Id
     * @return 业务是否成功
     */
    @Override
    public boolean contactUser(Long friendId) {
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.usersApi.saveUsers(userId,friendId))
                .map(id -> this.huanXinApi.addUserFriend(userId,friendId))
                .orElse(false);
    }

    /**
     * 查询联系人列表
     *
     * @param page     页数
     * @param pageSize 每页条数
     * @param keyword  关键字
     * @return 联系人分页列表
     */
    @Override
    public PageResult queryContactsList(Integer page, Integer pageSize, String keyword) {
        //-------------------init----------------------
        //好友列表
        List<Users> usersList;
        //好友基本信息列表
        List<UserInfo> userInfoList;
        //联系人列表
        List<UsersVo> contactsList = new ArrayList<>();

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        Long userId = UserThreadLocal.get();


        if (StringUtils.isNotEmpty(keyword)) {
            //关键不为空，查询所有的好友，在后面进行关键字过滤
            usersList = this.usersApi.queryAllUsersList(userId);
        } else {
            //关键字为空，进行分页查询

            usersList = Optional.ofNullable(this.usersApi.queryUsersList(userId, page, pageSize))
                    .map(PageInfo::getRecords).orElse(null);
        }

        if (CollUtil.isEmpty(usersList)) {
            return pageResult;
        }

        //获取好友ID列表
        List<Object> friendIds = CollUtil.getFieldValues(usersList, "friendId");



        if (StringUtils.isNotEmpty(keyword)) {
            userInfoList = this.userInfoService.queryByNickname(friendIds, keyword);
        }else {
            userInfoList = this.userInfoService.queryUserInfoList(friendIds);
        }

        if(CollUtil.isEmpty(userInfoList)){
            return pageResult;
        }


        //填充用户基本信息
        userInfoList.forEach(userInfo -> {
            UsersVo usersVo = new UsersVo();
            usersVo.setId(userInfo.getUserId());
            usersVo.setAge(userInfo.getAge());
            usersVo.setAvatar(userInfo.getLogo());
            usersVo.setGender(userInfo.getSex().name().toLowerCase());
            usersVo.setNickname(userInfo.getNickName());
            //环信用户账号
            usersVo.setUserId("HX_" + userInfo.getUserId());
            usersVo.setCity(StringUtils.substringBefore(userInfo.getCity(), "-"));
            contactsList.add(usersVo);
        });

        pageResult.setItems(contactsList);
        return pageResult;
    }

    /**
     * 查询消息点赞列表
     *
     * @param page     页数
     * @param pageSize 每页条数
     * @return 点赞列表
     */
    @Override
    public PageResult queryLikeCommentList(Integer page, Integer pageSize) {
        //init
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);

        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //RPC调用查询
        List<Comment> likes = Optional.ofNullable(this.quanZiApi.queryLikeCommentListByUser(userId, page, pageSize))
                .map(PageInfo::getRecords).orElse(null);

        if(CollUtil.isEmpty(likes)){
            return pageResult;
        }

        this.fillUserCommentList(likes,pageResult);
        return pageResult;
    }

    /**
     * 查询消息喜欢列表
     *
     * @param page     页数
     * @param pageSize 每页条数
     * @return 喜欢列表
     */
    @Override
    public PageResult queryLoveCommentList(Integer page, Integer pageSize) {
        //init
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);
        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //RPC调用查询
        List<Comment> loves = Optional.ofNullable(this.quanZiApi.queryLoveCommentListByUser(userId, page, pageSize))
                .map(PageInfo::getRecords).orElse(null);

        if(CollUtil.isEmpty(loves)){
            return pageResult;
        }

        this.fillUserCommentList(loves,pageResult);
        return pageResult;

    }

    /**
     * 查询消息评论列表
     *
     * @param page     页数
     * @param pageSize 每页条数
     * @return 评论列表
     */
    @Override
    public PageResult queryUserCommentList(Integer page, Integer pageSize) {
        //init
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);
        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //RPC调用查询
        List<Comment> comments = Optional.ofNullable(this.quanZiApi.queryCommentListByUser(userId, page, pageSize))
                .map(PageInfo::getRecords).orElse(null);

        if(CollUtil.isEmpty(comments)){
            return pageResult;
        }

        this.fillUserCommentList(comments,pageResult);
        return pageResult;
    }

    /**
     * 查询公告列表
     *
     * @param page     页数
     * @param pageSize 每页条数
     * @return 公告列表
     */
    @Override
    public PageResult queryMessageAnnouncementList(Integer page, Integer pageSize) {

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        List<Announcement> records = Optional.ofNullable(this.announcementApi.queryList(page, pageSize))
                .map(IPage::getRecords).orElse(null);

        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        List<AnnouncementVo> announcementVoList = new ArrayList<>();
        records.forEach(record -> {
            AnnouncementVo announcementVo = new AnnouncementVo();
            announcementVo.setId(record.getId().toString());
            announcementVo.setTitle(record.getTitle());
            announcementVo.setDescription(record.getDescription());
            announcementVo.setCreateDate(DateUtil.format(record.getCreated(), "yyyy-MM-dd HH:mm"));

            announcementVoList.add(announcementVo);
        });

        pageResult.setItems(announcementVoList);

        return pageResult;
    }

    /**
     * 删除好友
     *
     * @param userId 好友id
     */
    @Override
    public void removeUser(Long userId) {
        Long myUserId = UserThreadLocal.get();

        Boolean result = Optional.ofNullable(this.usersApi.removeUsers(myUserId, userId))
                .orElse(false);
        if(result){
            //将环信平台的好友关系解除
            this.huanXinApi.removeUserFriend(myUserId, userId);
        }
    }


    private void fillUserCommentList(List<Comment> records,PageResult pageResult){
        List<Object> userIds = CollUtil.getFieldValues(records, "userId");
        Map<Long, UserInfo> userInfoMap = this.userInfoService.queryUserInfoList(userIds)
                .stream().collect(Collectors.toMap((UserInfo::getUserId),
                Function.identity()));
        List<MessageCommentVo> messageCommentVoList = new ArrayList<>();

        records.forEach(record -> {
            UserInfo userInfo = userInfoMap.get(record.getUserId());
            MessageCommentVo messageCommentVo = new MessageCommentVo();
            messageCommentVo.setId(record.getId().toHexString());
            messageCommentVo.setAvatar(userInfo.getLogo());
            messageCommentVo.setNickname(userInfo.getNickName());
            messageCommentVo.setCreateDate(DateUtil.format(new Date(record.getCreated()), "yyyy-MM-dd HH:mm"));

            messageCommentVoList.add(messageCommentVo);
        });

        pageResult.setItems(messageCommentVoList);
    }
}

package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.*;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.UserLike;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Visitors;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BlackList;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Settings;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.*;
import top.yusora.tanhua.server.vo.*;
import top.yusora.tanhua.utils.TriFunction;
import top.yusora.tanhua.utils.UserThreadLocal;
import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * @author heyu
 */
@Service
@Slf4j
public class MyCenterServiceImpl implements MyCenterService {
    @Autowired
    private UserInfoService userInfoService;

    @DubboReference(version = "1.0.0")
    private UserLikeApi userLikeApi;

    @DubboReference(version = "1.0.0")
    private VisitorsApi visitorsApi;

    @DubboReference(version = "1.0.0")
    private UserApi userApi;

    @DubboReference(version = "1.0.0")
    private SettingsApi settingsApi;

    @DubboReference(version = "1.0.0")
    private QuestionApi questionApi;

    @DubboReference(version = "1.0.0")
    private BlackListApi blackListApi;

    @Autowired
    private IMService imService;

    @Autowired
    private RecommendUserService recommendUserService;

    @Autowired
    private TanhuaService tanhuaService;

    private final Map<Integer, TriFunction<Long, Integer, Integer, List<Object>>> actionMap = new HashMap<>(4);


    @PostConstruct
    public void init(){
        //查询互相喜欢列表中对方的用户id
        actionMap.put(1,(userId,page,pageSize) -> {
            PageInfo<UserLike> pageInfo = this.userLikeApi.queryMutualLikeList(userId, page, pageSize);
            return CollUtil.getFieldValues(pageInfo.getRecords(), "userId");
        });

        //查询出我喜欢的列表中对方用户id
        actionMap.put(2,(userId,page,pageSize) -> {
            PageInfo<UserLike> pageInfo = this.userLikeApi.queryLikeList(userId, page, pageSize);
            return CollUtil.getFieldValues(pageInfo.getRecords(), "likeUserId");
        });

        //查询出我的粉丝列表中 对方用户id
        actionMap.put(3,(userId,page,pageSize) -> {
            PageInfo<UserLike> pageInfo = this.userLikeApi.queryFanList(userId, page, pageSize);
            return CollUtil.getFieldValues(pageInfo.getRecords(), "userId");
        });

        //查询出访客列表中 访客用户id
        actionMap.put(4,(userId,page,pageSize) -> {
            PageInfo<Visitors> pageInfo = this.visitorsApi.topVisitor(userId, page, pageSize);
            return CollUtil.getFieldValues(pageInfo.getRecords(), "visitorUserId");
        });

    }
    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id，如果为空，表示查询当前登录人的信息
     * @return 用户基本信息数据
     */
    @Override
    public UserInfoVo queryUserInfoByUserId(Long userId) {
        if (ObjectUtil.isEmpty(userId)) {
            //如果查询id为null，就表示查询当前用户信息
            userId = UserThreadLocal.get();
        }

        //TODO:查询用户信息
        return Optional.ofNullable(this.userInfoService.queryUserInfoByUserId(userId))
                .map(userInfo -> {
                    UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class, "marriage");
                    userInfoVo.setGender(userInfo.getSex().name().toLowerCase());
                    userInfoVo.setMarriage(StrUtil.equals("已婚", userInfo.getMarriage()) ? 1 : 0);
                    return userInfoVo;
                }).orElse(null);
    }

    /**
     * 是否喜欢
     *
     * @param userId 对方用户id
     * @return 是否喜欢
     */
    @Override
    public Boolean isLike(Long userId) {
        Long myUserId = UserThreadLocal.get();

        return Optional.ofNullable(this.userLikeApi.isLike(myUserId,userId))
                .orElse(false);
    }

    /**
     * 更新用户信息
     *
     * @param userInfoVo 需更新的用户基本信息
     * @return 业务是否成功
     */
    @Override
    public Boolean updateUserInfo(UserInfoVo userInfoVo) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(userInfoVo.getNickname());
        userInfo.setAge(this.calAge(userInfoVo.getBirthday()));
        userInfo.setSex(StringUtils.equalsIgnoreCase(userInfoVo.getGender(), "man") ? SexEnum.MAN : SexEnum.WOMAN);
        userInfo.setBirthday(userInfoVo.getBirthday());
        userInfo.setCity(userInfoVo.getCity());
        userInfo.setEdu(userInfoVo.getEducation());
        userInfo.setIncome(StringUtils.replace(userInfoVo.getIncome(),"K",""));
        userInfo.setIndustry(userInfoVo.getProfession());
        userInfo.setMarriage(userInfoVo.getMarriage() == 1 ? "已婚" : "未婚");
        return this.userInfoService.updateUserInfoByUserId(userInfo);
    }

    /**
     * 查询 喜欢，互相关注，粉丝数量
     *
     * @return 喜欢，互相关注，粉丝数量
     */
    @Override
    public CountsVo queryCounts() {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        CountsVo countsVo = new CountsVo();

        countsVo.setEachLoveCount(Optional.ofNullable(this.userLikeApi.queryMutualLikeCount(userId))
        .orElse(0L));
        countsVo.setFanCount(Optional.ofNullable(this.userLikeApi.queryFanCount(userId))
        .orElse(0L));
        countsVo.setLoveCount(Optional.ofNullable(this.userLikeApi.queryLikeCount(userId))
        .orElse(0L));

        return countsVo;
    }

    /**
     * 互相关注、我关注、粉丝、谁看过我 - 翻页列表
     *
     * @param type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
     * @param page     当前页数
     * @param pageSize 每页显示条数
     * @param nickname 昵称
     * @return 相应用户列表
     */
    @Override
    public PageResult queryLikeList(Integer type, Integer page, Integer pageSize, String nickname) {
        //init
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //Get User Context
        Long userId = UserThreadLocal.get();

        if(type < 1 || type > 4){
            log.error("传入参数不合法，type: {}", type);
            return pageResult;
        }
        //Get Correspondent type's userIds
        List<Object> userIdList = this.actionMap.get(type).apply(userId, page, pageSize);

        if(CollUtil.isEmpty(userIdList)){
            return pageResult;
        }

        List<UserInfo> userInfoList = null;

        if(StrUtil.isNotEmpty(nickname)) {
            userInfoList = this.userInfoService.queryByNickname(userIdList, nickname);
        }else{
            userInfoList = this.userInfoService.queryUserInfoList(userIdList);
        }

        List<UserLikeListVo> userLikeListVos = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            UserLikeListVo userLikeListVo = new UserLikeListVo();
            userLikeListVo.setAge(userInfo.getAge());
            userLikeListVo.setAvatar(userInfo.getLogo());
            userLikeListVo.setCity(userInfo.getCity());
            userLikeListVo.setEducation(userInfo.getEdu());
            userLikeListVo.setGender(userInfo.getSex().name().toLowerCase());
            userLikeListVo.setId(userInfo.getUserId());
            userLikeListVo.setMarriage(StringUtils.equals(userInfo.getMarriage(), "已婚") ? 1 : 0);
            userLikeListVo.setNickname(userInfo.getNickName());
            //是否喜欢  userLikeApi中的isLike开放出来
            userLikeListVo.setAlreadyLove(this.userLikeApi.isLike(userId, userInfo.getUserId()));


            Long score = this.recommendUserService.queryScore(userId, userInfo.getUserId());
            userLikeListVo.setMatchRate(Convert.toInt(score));

            userLikeListVos.add(userLikeListVo);
        }

        pageResult.setItems(userLikeListVos);

        return pageResult;
    }

    /**
     * 取消喜欢
     *
     * @param userId 对方用户id
     */
    @Override
    public void disLike(Long userId) {
        //判断当前用户与此用户是否相互喜欢
        Long myUserId = UserThreadLocal.get();
        Boolean mutualLike = this.userLikeApi.isMutualLike(myUserId, userId);
        //取消喜欢
        this.userLikeApi.notLikeUser(myUserId, userId);

        if(mutualLike){
            //取消好友关系，解除在环信平台的好友关系
            this.imService.removeUser(userId);
        }
    }

    /**
     * 关注粉丝
     *
     * @param userId 对方用户id
     */
    @Override
    public void likeFan(Long userId) {
        //喜欢用户，如果用户是相互喜欢的话就会成为好友
        this.tanhuaService.likeUser(userId);
    }

    /**
     * 查询配置
     *
     * @return 用户配置信息
     */
    @Override
    public SettingsVo querySettings() {
        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //init
        SettingsVo settingsVo = new SettingsVo();
        //设置用户的基本信息
        settingsVo.setId(userId);
        settingsVo.setPhone(this.userApi.queryById(userId).getMobile());

        //查询用户的配置数据
        Settings settings = this.settingsApi.querySettings(userId);
        if(ObjectUtil.isNotEmpty(settings)){
            settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
            settingsVo.setLikeNotification(settings.getLikeNotification());
            settingsVo.setPinglunNotification(settings.getPinglunNotification());
        }

        //查询陌生人问题
        settingsVo.setStrangerQuestion(this.tanhuaService.queryQuestion(userId));

        return settingsVo;
    }

    /**
     * 设置陌生人问题
     *
     * @param content 问题内容
     */
    @Override
    public void saveQuestions(String content) {
        Long userId = UserThreadLocal.get();
        this.questionApi.save(userId,content);
    }

    /**
     * 查询黑名单
     *
     * @param page     当前页数
     * @param pagesize 每页条数
     * @return 黑名单分页列表
     */
    @Override
    public PageResult queryBlacklist(Integer page, Integer pagesize) {
        Long userId = UserThreadLocal.get();

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pagesize);

        IPage<BlackList> blacklist = this.blackListApi.queryBlacklist(userId, page, pagesize);

        pageResult.setCounts(Convert.toInt(blacklist.getTotal()));
        pageResult.setPages(Convert.toInt(blacklist.getPages()));

        List<BlackList> records = blacklist.getRecords();
        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        List<Object> userIds = CollUtil.getFieldValues(records, "blackUserId");
        List<UserInfo> userInfos = this.userInfoService.queryUserInfoList(userIds);

        List<BlackListVo> blackListVos = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            BlackListVo blackListVo = new BlackListVo();
            blackListVo.setAge(userInfo.getAge());
            blackListVo.setAvatar(userInfo.getLogo());
            blackListVo.setGender(userInfo.getSex().name().toLowerCase());
            blackListVo.setId(userInfo.getUserId());
            blackListVo.setNickname(userInfo.getNickName());

            blackListVos.add(blackListVo);
        }

        pageResult.setItems(blackListVos);

        return pageResult;
    }

    /**
     * 移除黑名单
     *
     * @param userId 用户id
     */
    @Override
    public void delBlacklist(Long userId) {
        Long myUserId = UserThreadLocal.get();
        this.blackListApi.delBlacklist(myUserId,userId);
    }

    /**
     * 更新通知参数
     *
     * @param likeNotification    推送喜欢通知
     * @param pinglunNotification 推送评论通知
     * @param gonggaoNotification 推送公告通知
     */
    @Override
    public void updateNotification(Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification) {
        Long userId = UserThreadLocal.get();

    }


    /**
     * 根据生日计算年龄
     * @param birthday 生日
     * @return 年龄
     */
    private Integer calAge(String birthday){
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(birthday, pattern);
        int year = date.getYear();
        int yearOfNow = LocalDate.now().getYear();
        return yearOfNow-year;
    }
}

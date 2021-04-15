package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.*;
import top.yusora.tanhua.dubbo.server.enums.HuanXinMessageType;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.dto.UserLocationDto;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Question;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.IMService;
import top.yusora.tanhua.server.service.RecommendUserService;
import top.yusora.tanhua.server.service.TanhuaService;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.vo.NearUserVo;
import top.yusora.tanhua.server.vo.TodayBest;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.*;

/**
 * @author heyu
 */
@Slf4j
@Service
public class TanhuaServiceImpl implements TanhuaService {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RecommendUserService recommendUserService;

    @Autowired
    private IMService imService;

    @Value("${tanhua.default.recommend.users}")
    String defaultRecommendUsers;

    @DubboReference(version = "1.0.0")
    private VisitorsApi visitorsApi;

    @DubboReference(version = "1.0.0")
    private QuestionApi questionApi;

    @DubboReference(version = "1.0.0")
    private HuanXinApi huanXinApi;

    @DubboReference(version = "1.0.0")
    private UserLocationApi userLocationApi;

    @DubboReference(version = "1.0.0")
    private RecommendUserApi recommendUserApi;

    @DubboReference(version = "1.0.0")
    private UserLikeApi userLikeApi;


    private final static String DEFAULT_QUESTION = "你的爱好是什么？";

    private final static String MAN = "man";

    private final static String WOMAN = "woman";

    /**
     * 根据用户ID查询相应用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public TodayBest queryUserInfo(Long userId) {


        return Optional.ofNullable(this.userInfoService.queryUserInfoByUserId(userId))
                .map(userInfo -> {
                    TodayBest todayBest = BeanUtil.toBeanIgnoreError(userInfo, TodayBest.class);
                    todayBest.setGender(userInfo.getSex().name().toLowerCase());
                    //缘分值
                    Long myUserId = UserThreadLocal.get();
                    todayBest.setFateValue(this.recommendUserService.queryScore(userId, myUserId));

                    //记录来访用户
                    this.visitorsApi.saveVisitor(userId, myUserId, "个人主页");

                    return todayBest;
                }).orElse(null);
    }

    /**
     * 查询陌生人问题
     *
     * @param userId 陌生人Id
     * @return @nonnull 问题
     */
    @Override
    public String queryQuestion(Long userId) {
        return Optional.ofNullable(this.questionApi.queryQuestion(userId))
                .map(Question::getTxt).orElse(DEFAULT_QUESTION);
    }

    /**
     * 回复陌生人问题 （聊一下）
     *
     * @param userId 陌生人ID
     * @param reply  回复内容
     * @return 是否发送成功
     */
    @Override
    public Boolean replyQuestion(Long userId, String reply) {
        //获取用户上下文
        Long myUserId = UserThreadLocal.get();

        //获取当前用户信息
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(myUserId);

        //构建消息内容
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", myUserId);
        msg.put("huanXinId", "HX_" + myUserId);
        msg.put("nickname", userInfo.getNickName());
        msg.put("strangerQuestion", this.queryQuestion(userId));
        msg.put("reply", reply);

        //发送环信消息给目标用户
        return this.huanXinApi.sendMsgFromAdmin("HX_" + userId,
                HuanXinMessageType.TXT, JSONUtil.toJsonStr(msg));
    }

    /**
     * 搜附近
     *
     * @param gender   性别
     * @param distance 距离
     * @return 附近的人列表
     */
    @Override
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();

        //获取用户位置
        UserLocationDto myLocation = this.userLocationApi.queryByUserId(userId);

        if(ObjectUtil.isNull(myLocation)){
            // TODO 默认
            return Collections.emptyList();
        }
        //获取附近的人列表
        PageInfo<UserLocationDto> pageInfo = this.userLocationApi.queryUserFromLocation(myLocation.getLongitude(),
                myLocation.getLatitude(), Convert.toDouble(distance), 1, 50);
        List<UserLocationDto> records = pageInfo.getRecords();

        if(CollUtil.isEmpty(records)){
            return Collections.emptyList();
        }

        SexEnum sexEnum = SexEnum.UNKNOWN;
        if (StrUtil.equalsIgnoreCase(gender, MAN)) {
            sexEnum = SexEnum.MAN;
        } else if (StrUtil.equalsIgnoreCase(gender, WOMAN)) {
            sexEnum = SexEnum.WOMAN;
        }

        //获取用户信息Map
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"),sexEnum);

        List<NearUserVo> result = new ArrayList<>();
        records.forEach(record -> {
            //排除自己
            if (!ObjectUtil.equal(record.getUserId(), userId)) {
                UserInfo userInfo = userInfoMap.get(record.getUserId());
                result.add(BeanUtil.toBeanIgnoreError(userInfo, NearUserVo.class));
            }
        });

        return result;
    }


    /**
     * 查询推荐卡片列表，从推荐列表中随机选取10个用户
     * @return 推荐卡片列表
     */
    @Override
    public List<TodayBest> queryCardsList() {
        Long myUserId = UserThreadLocal.get();
        List<RecommendUser> recommendUserList = this.recommendUserApi.queryCardList(myUserId, 50);
        if (CollUtil.isEmpty(recommendUserList)) {
            //默认推荐列表
            recommendUserList = new ArrayList<>();
            String[] userIds = defaultRecommendUsers.split(",");
            for (String userId : userIds) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(userId));
                recommendUser.setToUserId(myUserId);
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));
                recommendUserList.add(recommendUser);
            }
        }

        //随机选取10个进行展现
        int count = Math.min(10, recommendUserList.size());

        //随机打乱顺序
        Collections.shuffle(recommendUserList);
        //截取集合
        List<RecommendUser> subRecommendList = CollUtil.sub(recommendUserList, 0, count);

        List<Object> userIdList = CollUtil.getFieldValues(subRecommendList, "userId");
        //获取用户基本信息字典
        Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(userIdList);

        //排序
        List<TodayBest> todayBestList = new ArrayList<>();

        subRecommendList.forEach(recommendUser -> {
            UserInfo userInfo = userInfoMap.get(recommendUser.getUserId());
            TodayBest todayBest = BeanUtil.toBeanIgnoreError(userInfo, TodayBest.class);
            todayBest.setGender(userInfo.getSex().name().toLowerCase());
            todayBest.setFateValue(Convert.toLong(recommendUser.getScore()));
            todayBestList.add(todayBest);
        });
        return todayBestList;
    }

    /**
     * 喜欢:右滑
     *
     * @param likeUserId 对方用户Id
     * @return 业务 是否成功 nonnull
     */
    @Override
    public Boolean likeUser(Long likeUserId) {

        //获取用户上下文
        Long userId = UserThreadLocal.get();
        Boolean result = Optional.ofNullable(this.userLikeApi.likeUser(userId, likeUserId))
                .orElse(false);

        if (result) {
            //是否相互喜欢，如果是，成为好友
            if (Optional.ofNullable(this.userLikeApi.isMutualLike(userId, likeUserId))
                    .orElse(false)) {
                this.imService.contactUser(likeUserId);
            }
        }

        return result;
    }

    /**
     * 不喜欢：左滑
     *
     * @param likeUserId 对方用户id
     * @return 业务是否成功   nonnull
     */
    @Override
    public Boolean notLikeUser(Long likeUserId) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.userLikeApi.notLikeUser(userId, likeUserId))
                .orElse(false);
    }


}

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


    private final static String DEFAULT_QUESTION = "????????????????????????";

    private final static String MAN = "man";

    private final static String WOMAN = "woman";

    /**
     * ????????????ID????????????????????????
     *
     * @param userId ??????id
     * @return ????????????
     */
    @Override
    public TodayBest queryUserInfo(Long userId) {


        return Optional.ofNullable(this.userInfoService.queryUserInfoByUserId(userId))
                .map(userInfo -> {
                    TodayBest todayBest = BeanUtil.toBeanIgnoreError(userInfo, TodayBest.class);
                    todayBest.setGender(userInfo.getSex().name().toLowerCase());
                    //?????????
                    Long myUserId = UserThreadLocal.get();
                    todayBest.setFateValue(this.recommendUserService.queryScore(userId, myUserId));

                    //??????????????????
                    this.visitorsApi.saveVisitor(userId, myUserId, "????????????");

                    return todayBest;
                }).orElse(null);
    }

    /**
     * ?????????????????????
     *
     * @param userId ?????????Id
     * @return @nonnull ??????
     */
    @Override
    public String queryQuestion(Long userId) {
        return Optional.ofNullable(this.questionApi.queryQuestion(userId))
                .map(Question::getTxt).orElse(DEFAULT_QUESTION);
    }

    /**
     * ????????????????????? ???????????????
     *
     * @param userId ?????????ID
     * @param reply  ????????????
     * @return ??????????????????
     */
    @Override
    public Boolean replyQuestion(Long userId, String reply) {
        //?????????????????????
        Long myUserId = UserThreadLocal.get();

        //????????????????????????
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(myUserId);

        //??????????????????
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", myUserId);
        msg.put("huanXinId", "HX_" + myUserId);
        msg.put("nickname", userInfo.getNickName());
        msg.put("strangerQuestion", this.queryQuestion(userId));
        msg.put("reply", reply);

        //?????????????????????????????????
        return this.huanXinApi.sendMsgFromAdmin("HX_" + userId,
                HuanXinMessageType.TXT, JSONUtil.toJsonStr(msg));
    }

    /**
     * ?????????
     *
     * @param gender   ??????
     * @param distance ??????
     * @return ??????????????????
     */
    @Override
    public List<NearUserVo> queryNearUser(String gender, String distance) {
        //?????????????????????
        Long userId = UserThreadLocal.get();

        //??????????????????
        UserLocationDto myLocation = this.userLocationApi.queryByUserId(userId);

        if(ObjectUtil.isNull(myLocation)){
            // TODO ??????
            return Collections.emptyList();
        }
        //????????????????????????
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

        //??????????????????Map
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"),sexEnum);

        List<NearUserVo> result = new ArrayList<>();
        records.forEach(record -> {
            //????????????
            if (!ObjectUtil.equal(record.getUserId(), userId)) {
                UserInfo userInfo = userInfoMap.get(record.getUserId());
                result.add(BeanUtil.toBeanIgnoreError(userInfo, NearUserVo.class));
            }
        });

        return result;
    }


    /**
     * ?????????????????????????????????????????????????????????10?????????
     * @return ??????????????????
     */
    @Override
    public List<TodayBest> queryCardsList() {
        Long myUserId = UserThreadLocal.get();
        List<RecommendUser> recommendUserList = this.recommendUserApi.queryCardList(myUserId, 50);
        if (CollUtil.isEmpty(recommendUserList)) {
            //??????????????????
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

        //????????????10???????????????
        int count = Math.min(10, recommendUserList.size());

        //??????????????????
        Collections.shuffle(recommendUserList);
        //????????????
        List<RecommendUser> subRecommendList = CollUtil.sub(recommendUserList, 0, count);

        List<Object> userIdList = CollUtil.getFieldValues(subRecommendList, "userId");
        //??????????????????????????????
        Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(userIdList);

        //??????
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
     * ??????:??????
     *
     * @param likeUserId ????????????Id
     * @return ?????? ???????????? nonnull
     */
    @Override
    public Boolean likeUser(Long likeUserId) {

        //?????????????????????
        Long userId = UserThreadLocal.get();
        Boolean result = Optional.ofNullable(this.userLikeApi.likeUser(userId, likeUserId))
                .orElse(false);

        if (result) {
            //?????????????????????????????????????????????
            if (Optional.ofNullable(this.userLikeApi.isMutualLike(userId, likeUserId))
                    .orElse(false)) {
                this.imService.contactUser(likeUserId);
            }
        }

        return result;
    }

    /**
     * ??????????????????
     *
     * @param likeUserId ????????????id
     * @return ??????????????????   nonnull
     */
    @Override
    public Boolean notLikeUser(Long likeUserId) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.userLikeApi.notLikeUser(userId, likeUserId))
                .orElse(false);
    }


}

package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.RecommendUserService;
import top.yusora.tanhua.server.service.TodayBestService;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.RecommendUserQueryParam;
import top.yusora.tanhua.server.vo.TodayBest;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author heyu
 */
@Service
@Slf4j
public class TodayBestServiceImpl implements TodayBestService {


    @Autowired
    private RecommendUserService recommendUserService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${tanhua.sso.default.user}")
    private Long defaultUser;

    /**
     * 根据Token查找用户今日佳人
     * 1.查询出用户上下文（用户Id,手机号）
     * 2.根据用户ID从Mongodb中查询出今日佳人
     *
     * @param token JWT token
     * @return 今日佳人 id avatar nickname gender age tags fateValue
     */
    @Override
    public TodayBest queryTodayBest(String token) {
        //获取User对象，无需对User对象校验，其一定不为null
        Long userId = UserThreadLocal.get();

        //查询推荐用户（今日佳人）
        TodayBest todayBest = this.recommendUserService.queryTodayBest(userId);
        if(null == todayBest){
            //给出默认的推荐用户
            todayBest = new TodayBest();
            todayBest.setId(defaultUser);
            //固定值
            todayBest.setFateValue(80L);
        }

        //补全个人信息
        UserInfo userInfo = this.userInfoService.queryUserInfoByUserId(todayBest.getId());
        if(null == userInfo){
            return null;
        }

        infoFill(userInfo, todayBest);

        return todayBest;

    }

    /**
     * 根据用户Id分页查询 推荐用户 列表
     * @param token JWT token
     * @param queryParam 查询参数
     * @return 查询结果 失败为null
     */
    @Override
    public PageResult queryRecommendation(String token, RecommendUserQueryParam queryParam) {
        //获取User对象，无需对User对象校验，其一定不为null
        Long userId = UserThreadLocal.get();

        PageResult pageResult = new PageResult();

        //传递当前页
        pageResult.setPage(queryParam.getPage());
        //传递每页条数
        pageResult.setPagesize(queryParam.getPagesize());

        //----------------------以下业务若失败，pageResult仅返回 Page，Pagesize----------------------------
        //查询推荐列表 返回pageInfo
        PageInfo<RecommendUser> pageInfo = this.recommendUserService.queryRecommendUserList(userId,
                queryParam.getPage(), queryParam.getPagesize());

        List<RecommendUser> records = pageInfo.getRecords();

        if (CollectionUtils.isEmpty(records)) {
            //没有查询到推荐的用户列表
            return pageResult;
        }

        //填充个人信息

        //收集推荐用户的id
        List<Object> userIds = CollUtil.getFieldValues(records, "userId");


        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(userIds,queryParam);
        if(CollectionUtils.isEmpty(userInfoList)){
            //没有查询到用户的基本信息
            return pageResult;
        }

        //补全个人信息及缘分值
        List<TodayBest> todayBests = new ArrayList<>();

        //把推荐用户列表records转换成缘分值Map增加填充效率
        Map<Long, Double> scoreMap = records.stream().distinct().collect(Collectors
                .toMap(RecommendUser::getUserId, RecommendUser::getScore));

        userInfoList.forEach(userInfo -> {
            TodayBest todayBest = new TodayBest();
            infoFill(userInfo, todayBest);
            //缘分值
            Double score = scoreMap.get(userInfo.getUserId());
            double fateValue = Math.floor(score);

            todayBest.setFateValue(Double.valueOf(fateValue).longValue());
            todayBests.add(todayBest);
        });

        //按照缘分值进行倒序排序
        todayBests.sort((o1, o2) -> new Long(o2.getFateValue() - o1.getFateValue()).intValue());

        pageResult.setItems(todayBests);

        return pageResult;

    }

    private void infoFill(UserInfo userInfo, TodayBest todayBest) {
        todayBest.setId(userInfo.getUserId());
        todayBest.setAvatar(userInfo.getLogo());
        todayBest.setNickname(userInfo.getNickName());
        todayBest.setTags(StringUtils.split(userInfo.getTags(), ','));
        //链式调用可能NPE
        todayBest.setGender(Optional.ofNullable(userInfo.getSex())
                .map(SexEnum::getValue)
                .orElse(2)== 1 ? "man" : "woman");
        todayBest.setAge(userInfo.getAge());
    }
}

package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.RecommendUserApi;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.server.service.RecommendUserService;
import top.yusora.tanhua.server.vo.TodayBest;

import java.util.Optional;

/**
 * @author heyu
 */
@Service
@Slf4j
public class RecommendUserServiceImpl implements RecommendUserService {
    @DubboReference(interfaceClass = RecommendUserApi.class, version = "1.0.0")
    private RecommendUserApi recommendUserApi;

    /**
     * 根据用户Id查询今日佳人
     *
     * @param userId 用户ID
     * @return 今日佳人vo 失败：null
     */
    @Override
    public TodayBest queryTodayBest(Long userId) {
        RecommendUser recommendUser = this.recommendUserApi.queryWithMaxScore(userId);
        if(ObjectUtil.isNull(recommendUser)){
            return null;
        }
        Long uid = recommendUser.getUserId();
        Double score = recommendUser.getScore();
        if(!ObjectUtil.isAllNotEmpty(uid,score)){
            return null;
        }
        TodayBest todayBest = new TodayBest();
        todayBest.setId(uid);
        //缘分值,向下取整数
        score = Math.floor(score);
        todayBest.setFateValue(score.longValue());

        return todayBest;
    }

    /**
     * 根据 用户Id 分页查询 推荐用户 列表
     *
     * @param userId   用户ID
     * @param page     当前页
     * @param pagesize 每页条数
     * @return 推荐用户列表
     */
    @Override
    public PageInfo<RecommendUser> queryRecommendUserList(Long userId, Integer page, Integer pagesize) {
        return this.recommendUserApi.queryPageInfo(userId, page, pagesize);
    }

    /**
     * 查询推荐好友的缘分值
     *
     * @param userId   好友用户Id
     * @param toUserId 我的用户id
     * @return 缘分值
     */
    @Override
    public Long queryScore(Long userId, Long toUserId) {
        return Optional.ofNullable(this.recommendUserApi.queryScore(userId,toUserId))
                .map(score -> Convert.toLong(Math.floor(score),98L))
                .orElse(98L);
    }
}

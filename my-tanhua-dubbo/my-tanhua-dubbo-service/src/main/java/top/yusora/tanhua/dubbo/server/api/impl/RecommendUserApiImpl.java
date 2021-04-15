package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import top.yusora.tanhua.dubbo.server.api.RecommendUserApi;
import top.yusora.tanhua.dubbo.server.api.UserLikeApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Component
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserLikeApi userLikeApi;

    /**
     * 查询一位得分最高的推荐用户
     *
     * @param userId 用户ID
     * @return 今日佳人
     */
    @Override
    public RecommendUser queryWithMaxScore(Long userId) {
        Query query = Query.query(Criteria.where("toUserId").is(userId))
                .with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return this.mongoTemplate.findOne(query, RecommendUser.class);
    }

    /**
     * 按照得分倒序查询推荐列表
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 每页条数
     * @return 推荐列表
     */
    @Override
    public PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize) {
        //分页并且排序参数
        if(pageNum < 1){
            pageNum = 1;
        }
        PageRequest pageRequest =PageRequest.of(pageNum-1, pageSize, Sort.by(Sort.Order.desc("score")));
        //查询参数
        Query query = Query.query(Criteria.where("toUserId").is(userId)).with(pageRequest);
        List<RecommendUser> recommendUserList = this.mongoTemplate.find(query, RecommendUser.class);

        //暂时不提供数据总数
        return new PageInfo<>(0, pageNum, pageSize, recommendUserList);
    }


    /**
     * 查询推荐好友的缘分值
     *
     * @param userId 好友的id
     * @param toUserId 我的id
     * @return 好友的缘分值
     */
    @Override
    public Double queryScore(Long userId, Long toUserId) {
        Query query = Query.query(Criteria.where("toUserId").is(toUserId)
                .and("userId").is(userId));
        return Optional.ofNullable(this.mongoTemplate.findOne(query, RecommendUser.class))
                .map(RecommendUser::getScore).orElse(null);
    }

    /**
     * 查询探花列表，查询时需要排除喜欢和不喜欢的用户
     *
     * @param userId 用户Id
     * @param count  每次查询数量
     * @return 探花列表
     */
    @Override
    public List<RecommendUser> queryCardList(Long userId, Integer count) {
        Criteria criteria = Criteria.where("toUserId").is(userId);

        //排除喜欢和不喜欢列表的用户
        //查询喜欢列表
        List<Long> likeList = this.userLikeApi.queryLikeList(userId);
        //查询不喜欢列表
        List<Long> notLikeList = this.userLikeApi.queryNotLikeList(userId);

        List<Long> userIdList = new ArrayList<>();
        CollUtil.addAll(userIdList, likeList);
        CollUtil.addAll(userIdList, notLikeList);

        if (CollUtil.isNotEmpty(userIdList)) {
            criteria.andOperator(Criteria.where("userId").nin(userIdList));
        }

        Query query = Query.query(criteria)
                .with(PageRequest.of(0, count, Sort.by(Sort.Order.desc("score"))));
        return this.mongoTemplate.find(query, RecommendUser.class);
    }
}

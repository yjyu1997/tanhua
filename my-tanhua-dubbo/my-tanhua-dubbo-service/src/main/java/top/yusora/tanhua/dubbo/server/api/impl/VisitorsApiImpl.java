package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.RecommendUserApi;
import top.yusora.tanhua.dubbo.server.api.VisitorsApi;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Visitors;

import java.util.List;
import java.util.Optional;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
@Slf4j
public class VisitorsApiImpl implements VisitorsApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String VISITOR_REDIS_KEY = "VISITOR_USER";

    private static final Double DEFAULT_SCORE = 98D;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RecommendUserApi recommendUserApi;


    /**
     * 保存访客数据
     *
     * @param userId        我的id
     * @param visitorUserId 访客id
     * @param from          来源
     * @return 访客记录id @Nullable
     */
    @Override
    public String saveVisitor(Long userId, Long visitorUserId, String from) {
        //校验
        if (!ObjectUtil.isAllNotEmpty(userId, visitorUserId, from)) {
            return null;
        }

        //查询访客用户在今天是否已经记录过，如果已经记录过，不再记录
        String today = DateUtil.today();
        Long minDate = DateUtil.parseDateTime(today + " 00:00:00").getTime();
        Long maxDate = DateUtil.parseDateTime(today + " 23:59:59").getTime();

        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("visitorUserId").is(visitorUserId)
                //$and 同一个字段多个约束 不加是or的效果
                .andOperator(Criteria.where("date").gte(minDate),
                        Criteria.where("date").lte(maxDate)));

        long count = this.mongoTemplate.count(query, Visitors.class);
        if (count > 0) {
            //今天已经记录过的
            return null;
        }

        Visitors visitors = new Visitors();
        visitors.setFrom(from);
        visitors.setVisitorUserId(visitorUserId);
        visitors.setUserId(userId);
        visitors.setDate(System.currentTimeMillis());
        visitors.setId(ObjectId.get());

        //存储数据
        this.mongoTemplate.save(visitors);

        return visitors.getId().toHexString();
    }

    /**
     * 查询我的访客数据，存在2种情况：
     * 1. 我没有看过我的访客数据，返回前5个访客信息
     * 2. 之前看过我的访客，从上一次查看的时间点往后查询5个访客数据
     *
     * @param userId 我的id
     * @return 访客记录列表 @NotNull
     */
    @Override
    public List<Visitors> queryMyVisitor(Long userId) {
        // 查询前5个访客数据，按照访问时间倒序排序
        // 如果用户已经查询过列表，记录查询时间，后续查询需要按照这个时间往后查询

        // 上一次查询列表的时间
        Long date = Convert.toLong(this.redisTemplate.opsForHash().get(VISITOR_REDIS_KEY, String.valueOf(userId)));

        //查询前5个
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("date")));

        Query query = Query.query(Criteria.where("userId").is(userId))
                .with(pageRequest);

        //如果查询过 附加查询过的条件
        if (ObjectUtil.isNotEmpty(date)) {
            query.addCriteria(Criteria.where("date").gte(date));
        }

        List<Visitors> visitorsList = this.mongoTemplate.find(query, Visitors.class);

        visitorsList.forEach(visitors -> {
            Double score = Optional.ofNullable(this.recommendUserApi.queryScore(visitors.getVisitorUserId(), userId))
                    .orElse(DEFAULT_SCORE);
            visitors.setScore(score);
        });

        //将当前查询时间写入redis，在topVisitor中实现


        return visitorsList;
    }

    /**
     * 按照时间倒序排序，查询最近的访客信息
     *
     * @param userId   我的id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 访客分页列表
     */
    @Override
    public PageInfo<Visitors> topVisitor(Long userId, Integer page, Integer pageSize) {
        if(page < 1){
            page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Order.desc("date")));

        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageRequest);
        List<Visitors> visitorsList = this.queryList(query, userId);

        PageInfo<Visitors> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        if(CollUtil.isEmpty(visitorsList)){
            return pageInfo;
        }

        pageInfo.setRecords(visitorsList);

        //记录当前的时间到redis中，在首页查询时，就可以在这个时间之后查询了
        String hashKey = String.valueOf(userId);
        String value = String.valueOf(System.currentTimeMillis());
        this.redisTemplate.opsForHash().put(VISITOR_REDIS_KEY, hashKey, value);

        return pageInfo;

    }

    private List<Visitors> queryList(Query query, Long userId) {
        return this.mongoTemplate.find(query,Visitors.class);
    }
}

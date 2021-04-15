package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserLikeApi;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.UserLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0")
@Slf4j
@Service
public class UserLikeApiImpl implements UserLikeApi {

    public static final String LIKE_REDIS_KEY_PREFIX = "USER_LIKE_";

    public static final String NOT_LIKE_REDIS_KEY_PREFIX = "USER_NOT_LIKE_";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String getLikeRedisKey(Long userId) {
        return LIKE_REDIS_KEY_PREFIX + userId;
    }

    private String getNotLikeRedisKey(Long userId) {
        return NOT_LIKE_REDIS_KEY_PREFIX + userId;
    }

    private List<Long> setToList(Set<Object> keys) {
        List<Long> list = new ArrayList<>(keys.size());
        keys.forEach(o -> list.add(Convert.toLong(o)));
        return list;
    }


    private PageInfo<UserLike> queryList(Query query, Integer page, Integer pageSize) {
        //设置分页
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Order.desc("created")));
        query.with(pageRequest);

        List<UserLike> userLikeList = this.mongoTemplate.find(query, UserLike.class);

        PageInfo<UserLike> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        pageInfo.setRecords(userLikeList);

        return pageInfo;
    }


    private void saveMongoDB(Long userId, Long likeUserId, Boolean isLike) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("likeUserId").is(likeUserId));
        UserLike userLike = this.mongoTemplate.findOne(query, UserLike.class);

        if (ObjectUtil.isEmpty(userLike)) {
            //存储到MongoDB
            userLike = new UserLike();
            userLike.setId(ObjectId.get());
            userLike.setUserId(userId);
            userLike.setLikeUserId(likeUserId);
            userLike.setCreated(System.currentTimeMillis());
            userLike.setUpdated(userLike.getCreated());
            userLike.setIsLike(isLike);
            this.mongoTemplate.save(userLike);
        } else {
            //更新字段
            Update update = Update.update("isLike", isLike)
                    .set("updated", System.currentTimeMillis());
            this.mongoTemplate.updateFirst(query, update, UserLike.class);
        }
    }
    /**
     * 喜欢
     *
     * @param userId     自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    @Override
    public Boolean likeUser(Long userId, Long likeUserId) {
        //判断是否已经喜欢
        if (this.isLike(userId, likeUserId)) {
            return false;
        }

        //存储到Redis 和 MongoDB中

        //使用hash结构
        //用户1喜欢用户2：key -> USER_LIKE_1 value: 2, "1"
        //用户1喜欢用户3：key -> USER_LIKE_1 value: 3, "1"
        //用户5喜欢用户1：key -> USER_LIKE_5 value: 1, "1"

        String redisKey = this.getLikeRedisKey(userId);
        String hashKey = Convert.toStr(likeUserId);

        this.redisTemplate.opsForHash().put(redisKey, hashKey, "1");

        //判断之前是否有不喜欢
        if(this.isNotLike(userId, likeUserId)){
            redisKey = this.getNotLikeRedisKey(userId);
            hashKey = Convert.toStr(likeUserId);
            this.redisTemplate.opsForHash().delete(redisKey, hashKey);
        }

        //存储到MongoDB
        saveMongoDB(userId, likeUserId, true);
        return true;
    }




    /**
     * 不喜欢
     *
     * @param userId     自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    @Override
    public Boolean notLikeUser(Long userId, Long likeUserId) {
        if (this.isNotLike(userId, likeUserId)) {
            return false;
        }

        //存储到redis
        String redisKey = this.getNotLikeRedisKey(userId);
        String hashKey = Convert.toStr(likeUserId);
        this.redisTemplate.opsForHash().put(redisKey, hashKey, "1");

        //判断之前是否有喜欢
        if(this.isLike(userId, likeUserId)){
            redisKey = this.getLikeRedisKey(userId);
            hashKey = Convert.toStr(likeUserId);
            this.redisTemplate.opsForHash().delete(redisKey, hashKey);
        }

        //实现2种方式：
        //第一种：删除喜欢中的数据
        //第二种：保存到不喜欢表中
        //第三种：增加字段标识是否喜欢(采用)
        saveMongoDB(userId, likeUserId, false);
        return true;
    }

    /**
     * 是否喜欢
     *
     * @param userId     自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    @Override
    public Boolean isLike(Long userId, Long likeUserId) {
        String redisKey = this.getLikeRedisKey(userId);
        String hashKey = Convert.toStr(likeUserId);
        Boolean data = this.redisTemplate.opsForHash().hasKey(redisKey, hashKey);
        if(data){
            return true;
        }
        //redis不存在则复查mongodb
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("likeUserId").is(likeUserId));

        //不存在时返回false
        data = Optional.ofNullable(this.mongoTemplate.findOne(query, UserLike.class))
                .map(UserLike::getIsLike).orElse(false);

        if(data){
            //将喜欢放入缓存
            this.redisTemplate.opsForHash().put(redisKey, hashKey, "1");
        }
        return data;
    }

    /**
     * 是否不喜欢
     *
     * @param userId     自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    @Override
    public Boolean isNotLike(Long userId, Long likeUserId) {
        String redisKey = this.getNotLikeRedisKey(userId);
        String hashKey = Convert.toStr(likeUserId);
        Boolean data = this.redisTemplate.opsForHash().hasKey(redisKey, hashKey);
        if(data){
            return true;
        }

        //redis不存在则复查mongodb
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("likeUserId").is(likeUserId));

        //不存在时返回false
        data = !(Optional.ofNullable(this.mongoTemplate.findOne(query, UserLike.class))
                .map(UserLike::getIsLike).orElse(true));

        if(data){
            //将不喜欢放入缓存
            this.redisTemplate.opsForHash().put(redisKey, hashKey, "1");
        }
        return data;
    }

    /**
     * 是否相互喜欢
     *
     * @param userId     自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    @Override
    public Boolean isMutualLike(Long userId, Long likeUserId) {
        return this.isLike(userId, likeUserId) && this.isLike(likeUserId, userId);
    }

    /**
     * 查询喜欢列表
     *
     * @param userId 自己的用户id
     * @return 喜欢列表
     */
    @Override
    public List<Long> queryLikeList(Long userId) {
        String redisKey = this.getLikeRedisKey(userId);
        Set<Object> keys = this.redisTemplate.opsForHash().keys(redisKey);
        if(CollUtil.isNotEmpty(keys)){
            return setToList(keys);
        }

        //缓存没有，复查mongodb
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("isLike").is(true));

        List<UserLike> userLikes = this.mongoTemplate.find(query, UserLike.class);

        List<Long> results = userLikes.stream().map(UserLike::getLikeUserId).collect(Collectors.toList());

        results.forEach(result -> {
            this.redisTemplate.opsForHash().put(redisKey, Convert.toStr(result), "1");
        });

        return results;
    }

    /**
     * 查询不喜欢列表
     *
     * @param userId 自己的用户id
     * @return 不喜欢列表
     */
    @Override
    public List<Long> queryNotLikeList(Long userId) {
        String redisKey = this.getNotLikeRedisKey(userId);
        Set<Object> keys = this.redisTemplate.opsForHash().keys(redisKey);
        if(CollUtil.isNotEmpty(keys)){
            return setToList(keys);
        }

        //缓存没有，复查mongodb
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("isLike").is(false));

        List<UserLike> userNotLikes = this.mongoTemplate.find(query, UserLike.class);

        List<Long> results = userNotLikes.stream().map(UserLike::getLikeUserId).collect(Collectors.toList());

        results.forEach(result -> {
            this.redisTemplate.opsForHash().put(redisKey, Convert.toStr(result), "1");
        });
        return results;
    }

    /**
     * 相互喜欢的数量
     *
     * @param userId 用户id
     * @return 相互喜欢的数量
     */
    @Override
    public Long queryMutualLikeCount(Long userId) {
        //查询出自己喜欢的列表
        List<Long> myLikeList = this.queryLikeList(userId);

        //查询对方列表中是否有自己
        Long count = 0L;

        if(CollUtil.isEmpty(myLikeList)){
            return count;
        }

        for (Long likeUserId : myLikeList) {
            //“别人” 的喜欢列表中是否有 “我”
            if (this.isLike(likeUserId,userId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 喜欢数
     *
     * @param userId 用户id
     * @return 喜欢数
     */
    @Override
    public Long queryLikeCount(Long userId) {
        //查询出自己喜欢的列表
        List<Long> myLikeList = this.queryLikeList(userId);
        return Convert.toLong(myLikeList.size(),0L);
    }

    /**
     * 粉丝数
     *
     * @param userId 用户id
     * @return 粉丝数
     */
    @Override
    public Long queryFanCount(Long userId) {
        //无法通过redis查询完成，必须从Mongodb中查询
        Query query = Query.query(Criteria.where("likeUserId").is(userId));
        return this.mongoTemplate.count(query, UserLike.class);
    }

    /**
     * 分页查询相互喜欢列表
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 相互喜欢分页列表
     */
    @Override
    public PageInfo<UserLike> queryMutualLikeList(Long userId, Integer page, Integer pageSize) {
        //查询出自己喜欢的列表
        List<Long> myLikeList = this.queryLikeList(userId);

        //查询喜欢我的人
        Query query = Query.query(Criteria.where("userId").in(myLikeList)
                .and("likeUserId").is(userId)
        );

        return this.queryList(query, page, pageSize);
    }

    /**
     * 查询我喜欢的列表
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 我喜欢的分页列表
     */
    @Override
    public PageInfo<UserLike> queryLikeList(Long userId, Integer page, Integer pageSize) {
        //走缓存拿数据
        List<Long> likeList = this.queryLikeList(userId);
        //走索引（userID,likeUserId）速度更快
        //Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").in(likeList));
        PageInfo<UserLike> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        //手动分页减少一遍IO
        int[] startEnd = PageUtil.transToStartEnd(page - 1, pageSize);
        int start = startEnd[0];
        if(start > likeList.size()){
            return pageInfo;
        }
        int end = Math.min(startEnd[1],likeList.size());
        if(start > end){
            return pageInfo;
        }
        List<UserLike> userLikeList = new ArrayList<>();
        for (int i = start; i < end ; i++) {
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setLikeUserId(likeList.get(i));
            userLikeList.add(userLike);
        }
        pageInfo.setRecords(userLikeList);
        return pageInfo;
    }

    /**
     * 查询粉丝列表-对方喜欢我
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 粉丝分页列表
     */
    @Override
    public PageInfo<UserLike> queryFanList(Long userId, Integer page, Integer pageSize) {
        Query query = Query.query(Criteria.where("likeUserId").is(userId).and("isLike").is(true));
        return this.queryList(query, page, pageSize);
    }
}

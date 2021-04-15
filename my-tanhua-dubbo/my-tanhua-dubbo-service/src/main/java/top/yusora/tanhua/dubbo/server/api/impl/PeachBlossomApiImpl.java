package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.PeachBlossomApi;
import top.yusora.tanhua.dubbo.server.api.RecommendUserApi;
import top.yusora.tanhua.dubbo.server.api.UserLikeApi;
import top.yusora.tanhua.dubbo.server.enums.IdType;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Sound;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.UserLike;
import top.yusora.tanhua.dubbo.server.service.IdService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Slf4j
@Service
public class PeachBlossomApiImpl implements PeachBlossomApi {
    @Autowired
    private IdService idService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RecommendUserApi recommendUserApi;

    @Autowired
    private UserLikeApi userLikeApi;



    /**
     * 保存语音
     *
     * @param sound 语音
     * @return 保存成功后，返回语音id
     */
    @Override
    public String saveSound(Sound sound) {
        try {
            //校验
            if(!ObjectUtil.isAllNotEmpty(sound.getUserId(), sound.getSoundUrl())){
                return null;
            }

            //设置id
            sound.setId(ObjectId.get());
            //设置自增id for 推荐
            sound.setSid(this.idService.createId(IdType.SOUND));

            //发布时间
            sound.setCreated(System.currentTimeMillis());

            //保存到Mongodb中
            this.mongoTemplate.save(sound);

            return sound.getId().toHexString();
        } catch (Exception e) {
            log.error("语音发送失败~ sound = " + sound, e);
        }
        return null;
    }

    /**
     * 获取今日声音用户列表
     *
     * @param userId 用户Id
     * @return 声音列表
     */
    @Override
    public List<Long> getTodayList(Long userId) {

        //拉取8位推荐用户列表

        List<RecommendUser> recommendUsers = this.recommendUserApi.queryCardList(userId, 10);
        if(CollUtil.isEmpty(recommendUsers)){
            return Collections.emptyList();
        }
        return CollUtil.getFieldValues(recommendUsers, "userId", Long.class);
    }

    /**
     * 根据用户id 随机拉取一条语音
     *
     * @param userId 用户id
     * @param soundUserId 要查询的用户id
     * @return 语音
     */
    @Override
    public Sound getSound(Long userId, Long soundUserId) {

        if(!ObjectUtil.isAllNotEmpty(userId,soundUserId) || userId.equals(soundUserId)){
            return null;
        }

        Query query = Query.query(Criteria.where("userId").is(soundUserId));
        long count = this.mongoTemplate.count(query, Sound.class);
        if(count > 0){
            long skip = RandomUtil.randomLong(0, count);
            log.info("skip:   " +skip);
            query.skip(skip);
            return this.mongoTemplate.findOne(query,Sound.class);
        }

        //排除喜欢和不喜欢列表的用户
        //查询喜欢列表
        List<Long> likeList = this.userLikeApi.queryLikeList(userId);
        //查询不喜欢列表
        List<Long> notLikeList = this.userLikeApi.queryNotLikeList(userId);

        List<Long> userIdList = new ArrayList<>();
        CollUtil.addAll(userIdList, likeList);
        CollUtil.addAll(userIdList, notLikeList);
        userIdList.add(userId);
        if(CollUtil.isEmpty(userIdList)){
            return null;
        }
        //拉取最新一条语音
        Query queryRandom = Query.query(Criteria.where("userId").nin(userIdList)).with(Sort.by(Sort.Order.desc("created")));
        return this.mongoTemplate.findOne(queryRandom,Sound.class);
    }


}

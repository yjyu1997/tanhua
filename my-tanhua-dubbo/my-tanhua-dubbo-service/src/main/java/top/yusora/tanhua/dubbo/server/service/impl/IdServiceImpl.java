package top.yusora.tanhua.dubbo.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Publish;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Sound;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Video;
import top.yusora.tanhua.dubbo.server.service.IdService;
import top.yusora.tanhua.dubbo.server.enums.IdType;

import java.util.Optional;

/**
 *@author heyu
 */
@Service
public class IdServiceImpl implements IdService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;



    /**
     * 创建自增ID
     *
     * @param idType Id类型： PUBLISH VIDEO
     * @return 自增ID
     */
    @Override
    public Long createId(IdType idType) {
        String idKey = "TANHUA_ID_" + idType.toString();
        if(! Optional.ofNullable(this.redisTemplate.hasKey(idKey)).orElse(false)){
            if(idType.equals(IdType.PUBLISH)) {
                Query query = new Query().with(Sort.by(Sort.Order.desc("pid"))).limit(1);
                Long latest = Optional.ofNullable(this.mongoTemplate.findOne(query, Publish.class))
                        .map(Publish::getPid).orElse(0L);
                this.redisTemplate.opsForValue().set(idKey,Long.toString(latest));
            }else if(idType.equals(IdType.VIDEO)){
                Query query = new Query().with(Sort.by(Sort.Order.desc("vid"))).limit(1);
                Long latest = Optional.ofNullable(this.mongoTemplate.findOne(query, Video.class))
                        .map(Video::getVid).orElse(0L);
                this.redisTemplate.opsForValue().set(idKey,Long.toString(latest));
            }else{
                Query query = new Query().with(Sort.by(Sort.Order.desc("sid"))).limit(1);
                Long latest = Optional.ofNullable(this.mongoTemplate.findOne(query, Sound.class))
                        .map(Sound::getSid).orElse(0L);
                this.redisTemplate.opsForValue().set(idKey,Long.toString(latest));
            }
        }
        return this.redisTemplate.opsForValue().increment(idKey);
    }
}

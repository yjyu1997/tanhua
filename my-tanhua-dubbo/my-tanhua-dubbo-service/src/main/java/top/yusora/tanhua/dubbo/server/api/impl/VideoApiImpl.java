package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.DeleteResult;
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
import top.yusora.tanhua.dubbo.server.api.VideoApi;
import top.yusora.tanhua.dubbo.server.enums.IdType;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.FollowUser;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Video;
import top.yusora.tanhua.dubbo.server.service.IdService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Slf4j
@Service
public class VideoApiImpl implements VideoApi {

    @Autowired
    private IdService idService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String VIDEO_FOLLOW_USER_KEY_PREFIX = "VIDEO_FOLLOW_USER_";

    /**
     * 发布小视频
     *
     * @param video 小视频
     * @return 保存成功后，返回视频id
     */
    @Override
    public String saveVideo(Video video) {
        try {
            //校验
            if(!ObjectUtil.isAllNotEmpty(video.getUserId(), video.getPicUrl(), video.getVideoUrl())){
                return null;
            }

            //设置id
            video.setId(ObjectId.get());
            //设置自增id for 推荐
            video.setVid(this.idService.createId(IdType.VIDEO));

            //发布时间
            video.setCreated(System.currentTimeMillis());

            //保存到Mongodb中
            this.mongoTemplate.save(video);

            return video.getId().toHexString();
        } catch (Exception e) {
            log.error("小视频发布失败~ video = " + video, e);
        }
        return null;
    }


    /**
     * 分页查询小视频列表，按照时间倒序排序
     *
     * @param userId 用户ID
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @return 小视频列表
     */
    @Override
    public PageInfo<Video> queryVideoList(Long userId, Integer page, Integer pageSize) {
        PageInfo<Video> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        //从redis中获取推荐视频的数据
        String redisKey = "QUANZI_VIDEO_RECOMMEND_" + userId;
        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        List<Long> vids = new ArrayList<>();
        int recommendCount = 0;
        if (StrUtil.isNotEmpty(redisData)) {
            //---------------手动分页查询数据-------------------

            //总的推荐数据列表
            List<String> vidList = StrUtil.split(redisData, ',');
            //计算分页
            //[0, 10]
            int[] startEnd = PageUtil.transToStartEnd(page - 1, pageSize);
            //开始索引
            int startIndex = startEnd[0];
            //结束索引
            int endIndex = Math.min(startEnd[1], vidList.size());

            //添加进
            for (int i = startIndex; i < endIndex; i++) {
                vids.add(Convert.toLong(vidList.get(i)));
            }
            recommendCount = vidList.size();
        }

        if (CollUtil.isEmpty(vids)) {
            //没有推荐或前面推荐已经查询完毕，查询系统的视频数据

            //计算前面的推荐视频页数
            //e.g. 总共100 20条每页 查询第6页 前面已经查询完毕 totalPage=5
            // 数据库从第 6-5=1 页开始查询
            int totalPage = PageUtil.totalPage(recommendCount, pageSize);

            PageRequest pageRequest = PageRequest.of(page - totalPage - 1, pageSize, Sort.by(Sort.Order.desc("created")));
            Query query = new Query().with(pageRequest);
            List<Video> videoList = this.mongoTemplate.find(query, Video.class);
            pageInfo.setRecords(videoList);
            return pageInfo;
        }

        //根据vid查询对应的视频数据
        Query query = Query.query(Criteria.where("vid").in(vids));
        List<Video> videoList = this.mongoTemplate.find(query, Video.class);
        pageInfo.setRecords(videoList);

        return pageInfo;
    }

    /**
     * 根据id查询视频对象
     *
     * @param videoId 小视频id
     * @return 小视频
     */
    @Override
    public Video queryVideoById(String videoId) {
        return this.mongoTemplate.findById(new ObjectId(videoId),Video.class);
    }

    /**
     * 关注用户
     *
     * @param userId       当前用户
     * @param followUserId 关注的目标用户
     * @return 业务是否成功
     */
    @Override
    public Boolean followUser(Long userId, Long followUserId) {
        //校验入参
        if (!ObjectUtil.isAllNotEmpty(userId, followUserId)) {
            return false;
        }
        try{
            //需要将用户的关注列表，保存到redis中，方便后续的查询
            //使用redis的hash结构
            if (this.isFollowUser(userId, followUserId)) {
                //已经关注
                return false;
            }

            FollowUser followUser = new FollowUser();
            followUser.setId(ObjectId.get());
            followUser.setUserId(userId);
            followUser.setFollowUserId(followUserId);
            followUser.setCreated(System.currentTimeMillis());

            this.mongoTemplate.save(followUser);

            //保存数据到redis
            String redisKey = this.getVideoFollowUserKey(userId);
            String hashKey = String.valueOf(followUserId);

            this.redisTemplate.opsForHash().put(redisKey, hashKey, "1");
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消关注用户
     *
     * @param userId       当前用户
     * @param followUserId 关注的目标用户
     * @return 业务是否成功
     */
    @Override
    public Boolean disFollowUser(Long userId, Long followUserId) {
        //校验入参
        if (!ObjectUtil.isAllNotEmpty(userId, followUserId)) {
            return false;
        }

        if (!this.isFollowUser(userId, followUserId)) {
            //未关注该用户
            return false;
        }

        try {
            //取消关注，删除关注数据即可
            Query query = Query.query(Criteria.where("userId").is(userId)
                    .and("followUserId").is(followUserId));

            DeleteResult result = this.mongoTemplate.remove(query, FollowUser.class);
            if (result.getDeletedCount() > 0) {
                //同时删除redis中的数据
                String redisKey = this.getVideoFollowUserKey(userId);
                String hashKey = String.valueOf(followUserId);
                this.redisTemplate.opsForHash().delete(redisKey, hashKey);

                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询用户是否关注某个用户
     *
     * @param userId       当前用户
     * @param followUserId 关注的目标用户
     * @return 业务是否成功
     */
    @Override
    public Boolean isFollowUser(Long userId, Long followUserId) {
        String redisKey = this.getVideoFollowUserKey(userId);
        String hashKey = String.valueOf(followUserId);
        Object data = this.redisTemplate.opsForHash().get(redisKey, hashKey);
        if (ObjectUtil.isNotEmpty(data)) {
            return StrUtil.equals(Convert.toStr(data), "1");
        }
        //未命中则查询MongoDb进行确认
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("followUserId").is(followUserId));
        long count = this.mongoTemplate.count(query, FollowUser.class);
        if(count == 0){
            return false;
        }
        //如果MongoDb有该数据，则存入Redis
        //写入到redis中
        this.redisTemplate.opsForHash().put(redisKey, hashKey, "1");

        return true;
    }

    /**
     * 获取关注用户缓存RedisKey
     * @param userId 用户Id
     * @return redisKey
     */
    private String getVideoFollowUserKey(Long userId) {
        return VIDEO_FOLLOW_USER_KEY_PREFIX + userId;
    }
}


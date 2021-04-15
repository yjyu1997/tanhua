package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.QuanZiApi;
import top.yusora.tanhua.dubbo.server.api.VideoApi;
import top.yusora.tanhua.dubbo.server.enums.CommentType;
import top.yusora.tanhua.dubbo.server.enums.IdType;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.*;
import top.yusora.tanhua.dubbo.server.service.IdService;
import top.yusora.tanhua.dubbo.server.service.TimeLineService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
@Slf4j
public class QuanZiApiImpl implements QuanZiApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdService idService;

    @Autowired
    private TimeLineService timeLineService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private VideoApi videoApi;


    /**
     * @Description 评论数据存储在Redis中key的前缀
     */
    private static final String COMMENT_REDIS_KEY_PREFIX = "QUANZI_COMMENT_";

    /**
     * @Description 用户是否点赞的前缀
     */
    private static final String COMMENT_USER_LIKE_REDIS_KEY_PREFIX = "USER_LIKE_";

    /**
     * @Description 用户是否喜欢的前缀
     */
    private static final String COMMENT_USER_LOVE_REDIS_KEY_PREFIX = "USER_LOVE_";


    private final Map<QuanZiType, BiConsumer<String, Comment>> actionMap = new HashMap<>();

    @PostConstruct
    public void init(){
        this.actionMap.put(QuanZiType.PUBLISH,(var1,var2) -> {
            Optional<Publish> publishOpt = Optional.ofNullable(this.queryPublishById(var1));
            publishOpt.ifPresent(publish -> var2.setPublishUserId(publish.getUserId()));
        });

        this.actionMap.put(QuanZiType.COMMENT,(var1,var2) -> {
            Optional<Comment> commentOpt = Optional.ofNullable(this.queryCommentById(var1));
            commentOpt.ifPresent(parentComment -> var2.setPublishUserId(parentComment.getUserId()));
        });

        this.actionMap.put(QuanZiType.VIDEO,(var1,var2) -> {
            Optional<Video> videoOpt = Optional.ofNullable(this.videoApi.queryVideoById(var1));
            videoOpt.ifPresent(video -> var2.setPublishUserId(video.getUserId()));
        });
    }


    /**
     * 分页查询好友动态
     *
     * @param userId   用户id
     * @param page     当前页数
     * @param pageSize 每一页查询的数据条数
     * @return 好友动态列表
     */
    @Override
    public PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize) {
        //分析：查询好友的动态，实际上查询时间线表
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        //------------------MongoDB分页查询时间线表----------------------
        if(page < 1){
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Order.desc("date")));

        Query query = new Query().with(pageable);
        List<TimeLine> timeLineList = this.mongoTemplate.find(query, TimeLine.class, "quanzi_time_line_" + userId);

        if(CollUtil.isEmpty(timeLineList)){
            //没有查询到数据
            return pageInfo;
        }

        //获取时间线列表中的发布id的列表
        List<Object> publishIds = CollUtil.getFieldValues(timeLineList, "publishId");

        //---------------------根据发布ID查询发布表------------------------
        Query queryPublish = Query.query(Criteria.where("id").in(publishIds)).with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(queryPublish, Publish.class);

        //返回好友动态列表
        pageInfo.setRecords(publishList);
        return pageInfo;
    }



    /**
     * 发布动态
     *
     * @param publish 动态内容
     * @return 发布成功返回动态id
     */
    @Override
    public String savePublish(Publish publish) {
        //对publish对象校验
        if (!ObjectUtil.isAllNotEmpty(publish.getText(), publish.getUserId())) {
            //发布失败
            return null;
        }

        //设置主键id
        publish.setId(ObjectId.get());

        try{
            //设置自增长的pid（分布式需要使用Redis）
            publish.setPid(this.idService.createId(IdType.PUBLISH));
            publish.setCreated(System.currentTimeMillis());

            //写入到publish表中
            this.mongoTemplate.save(publish);

            //写入相册表
            Album album = new Album();
            album.setId(ObjectId.get());
            album.setCreated(System.currentTimeMillis());
            album.setPublishId(publish.getId());

            this.mongoTemplate.save(album, "quanzi_album_" + publish.getUserId());

            //写入好友的时间线表（异步写入）
            this.timeLineService.saveTimeLine(publish.getUserId(), publish.getId());

        }catch (Exception e){

            //TODO 需要做事务的回滚，Mongodb的单节点服务，不支持事务
            log.error("发布动态失败~ publish = " + publish, e);
            return null;
        }

         return publish.getId().toHexString();
    }



    /**
     * 分页查询推荐动态
     * 推荐动态Pid 是通过推荐系统计算出，写入Redis
     *
     * 格式：QUANZI_PUBLISH_RECOMMEND_{user_id} : 2562,3639,2063,3448,2128......
     *
     * @param userId   用户id
     * @param page     当前页数
     * @param pageSize 每一页查询的数据条数
     * @return 推荐动态列表
     */
    @Override
    public PageInfo<Publish> queryRecommendPublishList(Long userId, Integer page, Integer pageSize) {
        PageInfo<Publish> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        // 从Redis 查询推荐结果数据
        String key = "QUANZI_PUBLISH_RECOMMEND_" + userId;
        String data = this.redisTemplate.opsForValue().get(key);

        if (StrUtil.isEmpty(data)) {
            return pageInfo;
        }

        //---------------------查询到的pid进行分页处理---------------------
        List<String> pids = StrUtil.split(data, ',');

        //计算分页
        //[0, 10]
        int[] startEnd = PageUtil.transToStartEnd(page - 1, pageSize);

        //开始索引
        int startIndex = startEnd[0];
        //结束索引
        int endIndex = Math.min(startEnd[1], pids.size());

        //分页中的数据填入List
        List<Long> pidLongList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pidLongList.add(Long.valueOf(pids.get(i)));
        }

        if (CollUtil.isEmpty(pidLongList)) {
            //没有查询到数据
            return pageInfo;
        }

        //根据pid查询publish
        Query query = Query.query(Criteria.where("pid").in(pidLongList))
                .with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(query, Publish.class);

        if (CollUtil.isEmpty(publishList)) {
            //没有查询到数据
            return pageInfo;
        }

        pageInfo.setRecords(publishList);
        return pageInfo;
    }



    /**
     * 根据主键id查询动态
     *
     * @param id 动态id
     * @return 动态数据
     */
    @Override
    public Publish queryPublishById(String id) {
        return this.mongoTemplate.findById(new ObjectId(id), Publish.class);
    }


    /**
     * 点赞
     *
     * @param userId 用户ID
     * @param publishId 动态ID
     * @return 业务是否成功
     */
    @Override
    public Boolean likeComment(Long userId, String publishId, QuanZiType type) {
        //如果已经点赞，返回False
        if (this.queryUserIsLike(userId, publishId)) {
            return false;
        }

        //保存Comment数据：点赞 喜欢 评论 均视为评论数据
        Boolean result = this.saveComment(userId, publishId, CommentType.LIKE, null,type);
        if (!result) {
            //未保存成功，返回false
            return false;
        }

        //------------------修改redis中的点赞数以及是否点赞------------------
        //修改点赞数
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String hashKey = CommentType.LIKE.toString();
        //点赞数自增1
        this.redisTemplate.opsForHash().increment(redisKey, hashKey, 1);
        //修改用户点赞状态->点赞
        String userHashKey = this.getCommentUserLikeRedisKeyPrefix(userId);
        this.redisTemplate.opsForHash().put(redisKey, userHashKey, "1");
        return true;
    }

    /**
     * 取消点赞
     *
     * @param userId 当前操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    @Override
    public Boolean disLikeComment(Long userId, String publishId) {
        //判断用户是否已经点赞，如果没有点赞就返回
        if (!this.queryUserIsLike(userId, publishId)) {
            return false;
        }

        //删除评论数据
        Boolean result = this.removeComment(userId, publishId, CommentType.LIKE);
        if (!result) {
            return false;
        }

        //-----------------修改Redis中的数据 点赞数-1 点赞状态：删除HashKey-----------------
        //修改点赞数
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String hashKey = CommentType.LIKE.toString();
        //点赞数-1
        this.redisTemplate.opsForHash().increment(redisKey,hashKey,-1);
        //修改点赞状态
        String userHashKey = this.getCommentUserLikeRedisKeyPrefix(userId);
        this.redisTemplate.opsForHash().delete(redisKey, userHashKey);

        return true;
    }

    /**
     * 查询点赞数
     *
     * @param publishId 动态Id
     * @return 点赞数
     */
    @Override
    public Long queryLikeCount(String publishId) {
        //从Redis中命中查询，如果命中直接返回即可
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String hashKey = CommentType.LIKE.toString();
        Object data = this.redisTemplate.opsForHash().get(redisKey, hashKey);
        if (ObjectUtil.isNotEmpty(data)) {
            return Convert.toLong(data);
        }

        //未命中查询Mongodb
        Long count = this.queryCommentCount(publishId, CommentType.LIKE);
        //写入Redis中
        this.redisTemplate.opsForHash().put(redisKey, hashKey, String.valueOf(count));
        return count;
    }

    /**
     * 查询用户是否点赞该动态
     *
     * @param userId 操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    @Override
    public Boolean queryUserIsLike(Long userId, String publishId) {
        //从redis中查询数据
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String userHashKey = this.getCommentUserLikeRedisKeyPrefix(userId);
        Object data = this.redisTemplate.opsForHash().get(redisKey, userHashKey);
        if (ObjectUtil.isNotEmpty(data)) {
            return StrUtil.equals(Convert.toStr(data), "1");
        }
        //未命中则再次查询UserDb 进行确认
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(publishId))
                .and("userId").is(userId)
                .and("commentType").is(CommentType.LIKE)
        );
        long count = this.mongoTemplate.count(query, Comment.class);
        if(count == 0){
            return false;
        }
        //如果MongoDb有该数据，则存入Redis
        //写入到redis中
        this.redisTemplate.opsForHash().put(redisKey, userHashKey, "1");

        return true;
    }

    /**
     * 喜欢
     *
     * @param userId    操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    @Override
    public Boolean loveComment(Long userId, String publishId,QuanZiType type) {
        //查询该用户是否已经喜欢
        if (this.queryUserIsLove(userId, publishId)) {
            return false;
        }

        //保存Comment数据：点赞 喜欢 评论 均视为评论数据
        boolean result = this.saveComment(userId, publishId, CommentType.LOVE, null,type);
        if (!result) {
            //未保存成功，返回false
            return false;
        }

        //------------------修改redis中的点赞数以及是否点赞------------------
        //修改点赞数
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String hashKey = CommentType.LOVE.toString();
        //喜欢数自增1
        this.redisTemplate.opsForHash().increment(redisKey, hashKey, 1);
        //修改用户喜欢状态->已喜欢
        String userHashKey = this.getCommentUserLoveRedisKeyPrefix(userId);
        this.redisTemplate.opsForHash().put(redisKey, userHashKey, "1");
        return true;

    }

    /**
     * 取消喜欢
     *
     * @param userId    操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    @Override
    public Boolean disLoveComment(Long userId, String publishId) {
        //判断用户是否已经喜欢，如果没有点赞就返回
        if (!this.queryUserIsLove(userId, publishId)) {
            return false;
        }

        //删除评论数据
        Boolean result = this.removeComment(userId, publishId, CommentType.LOVE);

        if (!result) {
            return false;
        }

        //-----------------修改Redis中的数据 喜欢数-1 喜欢状态：删除HashKey-----------------
        //修改喜欢数
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String hashKey = CommentType.LOVE.toString();
        //喜欢数-1
        this.redisTemplate.opsForHash().increment(redisKey,hashKey,-1);
        //修改喜欢状态
        String userHashKey = this.getCommentUserLoveRedisKeyPrefix(userId);
        this.redisTemplate.opsForHash().delete(redisKey, userHashKey);

        return true;
    }

    /**
     * 查询喜欢数
     *
     * @param publishId 动态Id
     * @return 喜欢数
     */
    @Override
    public Long queryLoveCount(String publishId) {
        //首先从redis中命中，如果命中的话就返回，没有命中就查询Mongodb
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String hashKey = CommentType.LOVE.toString();
        Object value = this.redisTemplate.opsForHash().get(redisKey, hashKey);
        if (ObjectUtil.isNotEmpty(value)) {
            return Convert.toLong(value);
        }

        //未命中查询Mongodb
        Long count = this.queryCommentCount(publishId, CommentType.LOVE);
        //写入Redis中
        this.redisTemplate.opsForHash().put(redisKey, hashKey, String.valueOf(count));
        return count;

    }

    /**
     * 查询用户是否喜欢该动态
     *
     * @param userId    操作用户Id
     * @param publishId 动态Id
     * @return true：已喜欢 false：未喜欢
     */
    @Override
    public Boolean queryUserIsLove(Long userId, String publishId) {
        //首先从redis中命中，如果命中的话就返回，没有命中就查询Mongodb
        String redisKey = this.getCommentRedisKeyPrefix(publishId);
        String userHashKey = this.getCommentUserLoveRedisKeyPrefix(userId);
        Object data = this.redisTemplate.opsForHash().get(redisKey, userHashKey);
        if (ObjectUtil.isNotEmpty(data)) {
            return StrUtil.equals(Convert.toStr(data), "1");
        }
        //未命中则再次查询UserDb 进行确认
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(publishId))
                .and("userId").is(userId)
                .and("commentType").is(CommentType.LOVE)
        );
        long count = this.mongoTemplate.count(query, Comment.class);
        if(count == 0){
            //未喜欢
            return false;
        }
        //如果MongoDb有该数据，则存入Redis
        //写入到redis中
        this.redisTemplate.opsForHash().put(redisKey, userHashKey, "1");

        return true;
    }

    /**
     * 查询评论列表
     *
     * @param publishId 动态ID
     * @param page      页数
     * @param pageSize  每页条数
     * @return 评论列表
     */
    @Override
    public PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize) {
        //初始化pageInfo
        PageInfo<Comment> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        //--------------------分页查询Mongodb评论表--------------------

        //构造分页查询条件
        if (page < 1){
            page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.asc("created")));
        Query query = new Query(Criteria.where("publishId").is(new ObjectId(publishId))
        .and("commentType").is(CommentType.COMMENT.getType())).with(pageRequest);

        //查询评论列表
        List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);

        if(CollUtil.isEmpty(commentList)){
            return pageInfo;
        }

        //填入评论列表，返回
        pageInfo.setRecords(commentList);
        return pageInfo;
    }

    /**
     * 发表评论
     *
     * @param userId 发表评论者id
     * @param publishId 当前动态id
     * @param content 评论内容
     * @return 业务是否成功
     */
    @Override
    public Boolean saveComment(Long userId, String publishId, String content,QuanZiType type) {
        return this.saveComment(userId, publishId, CommentType.COMMENT, content, type);
    }

    /**
     * 查询评论数
     *
     * @param publishId 动态Id
     * @return 评论数
     */
    @Override
    public Long queryCommentCount(String publishId) {
        return this.queryCommentCount(publishId,CommentType.COMMENT);
    }

    /**
     * 查询对我的点赞消息列表
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 点赞消息列表
     */
    @Override
    public PageInfo<Comment> queryLikeCommentListByUser(Long userId, Integer page, Integer pageSize) {
        return this.queryCommentListByUser(userId,CommentType.LIKE,page,pageSize);
    }

    /**
     * 查询对我的喜欢消息列表
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 喜欢消息列表
     */
    @Override
    public PageInfo<Comment> queryLoveCommentListByUser(Long userId, Integer page, Integer pageSize) {
        return this.queryCommentListByUser(userId, CommentType.LOVE, page, pageSize);
    }

    /**
     * 查询对我的评论消息列表
     *
     * @param userId   用户Id
     * @param page     页数
     * @param pageSize 每页条数
     * @return 评论消息列表
     */
    @Override
    public PageInfo<Comment> queryCommentListByUser(Long userId, Integer page, Integer pageSize) {
        return this.queryCommentListByUser(userId, CommentType.COMMENT, page, pageSize);
    }

    /**
     * 查询相册表
     *
     * @param userId   用户Id
     * @param page     当前页数
     * @param pageSize 每页条数
     * @return 该用户发布的动态列表
     */
    @Override
    public PageInfo<Publish> queryAlbumList(Long userId, Integer page, Integer pageSize) {
        //init
        PageInfo<Publish> pageInfo = new PageInfo<>();
        if(page < 1){
            page = 1;
        }
        //查询相册表
        PageRequest pageRequest = PageRequest.of(page - 1 , pageSize,
                Sort.by(Sort.Order.desc("created")));
        Query query = new Query().with(pageRequest);

        List<Album> albumList = this.mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);

        if(CollUtil.isEmpty(albumList)){
            return pageInfo;
        }

        //根据发布id查询发布数据

        List<Object> publishIds = CollUtil.getFieldValues(albumList, "publishId");

        Query queryPublish =
                Query.query(Criteria.where("id").in(publishIds))
                        .with(Sort.by(Sort.Order.desc("created")));
        List<Publish> publishList = this.mongoTemplate.find(queryPublish, Publish.class);

        if(CollUtil.isEmpty(publishList)){
            return pageInfo;
        }

        pageInfo.setRecords(publishList);

        return pageInfo;
    }


    /**
     * 根据发布用户id，评论类型查询相应的评论列表
     * @param userId 用户id
     * @param commentType 评论类型
     * @param page 页数
     * @param pageSize 每页条数
     * @return 指定类型指定发布用户的评论消息列表
     */
    private PageInfo<Comment> queryCommentListByUser(Long userId, CommentType commentType, Integer page, Integer pageSize) {
        if(page < 0){
            page = 1;
        }

        PageInfo<Comment> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        PageRequest pageRequest = PageRequest.of(page - 1, pageSize,
                Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("publishUserId").is(userId)
                .and("commentType").is(commentType.getType())).with(pageRequest);

        List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);

        if(CollUtil.isEmpty(commentList)){
            return pageInfo;
        }

        pageInfo.setRecords(commentList);
        return pageInfo;
    }


    /**
     * 保存Comment
     * @param userId 操作用户Id
     * @param publishId 动态ID
     * @param commentType 评论类型
     * @param content 评论内容
     * @return 业务是否成功
     */
    private Boolean saveComment(Long userId, String publishId,
                                CommentType commentType, String content,QuanZiType type) {
        try {
            Comment comment = new Comment();
            comment.setId(ObjectId.get());
            comment.setUserId(userId);
            comment.setPublishId(new ObjectId(publishId));
            // 评论类型
            comment.setCommentType(commentType.getType());
            // 内容
            comment.setContent(content);
            comment.setCreated(System.currentTimeMillis());

            //查询对应父类型表 填入publishUserId -> comment
            this.actionMap.get(type).accept(publishId,comment);

            if(ObjectUtil.isEmpty(comment.getPublishUserId())){
                return false;
            }

            this.mongoTemplate.save(comment);
            return true;
        }catch (Exception e){
            log.error("保存Comment出错~ userId = " + userId + ", publishId = " + publishId + ", commentType = " + commentType, e);
        }
        return false;
    }

    /**
     *  根据id查询Comment对象
     * @param id Comment主键id
     * @return Comment对象
     */
    private Comment queryCommentById(String id) {
        return this.mongoTemplate.findById(new ObjectId(id), Comment.class);
    }


    /**
     * 删除评论数据
     *
     * @param userId 操作用户Id
     * @param publishId 动态Id
     * @param commentType 评论类型
     * @return 业务是否成功
     */
    private Boolean removeComment(Long userId, String publishId, CommentType commentType) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(publishId))
                .and("commentType").is(commentType.getType())
        );
        return this.mongoTemplate.remove(query, Comment.class).getDeletedCount() > 0;
    }

    /**
     * 查询选定评论类型评论数量
     *
     * @param publishId 动态Id
     * @param commentType 评论类型
     * @return 评论数量
     */
    private Long queryCommentCount(String publishId, CommentType commentType) {
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(publishId))
                .and("commentType").is(commentType.getType())
        );
        return this.mongoTemplate.count(query, Comment.class);
    }


    /**
     * 获取当前动态评论RedisKey
     * @param publishId 当前动态Id
     * @return RedisKey
     */
    private String getCommentRedisKeyPrefix(String publishId) {
        return COMMENT_REDIS_KEY_PREFIX + publishId;
    }


    /**
     * 获取当前动态评论RedisKey下 当前用户 是否点赞的RedisKey
     * @param userId 当前用户Id
     * @return RedisKey
     */
    private String getCommentUserLikeRedisKeyPrefix(Long userId) {
        return COMMENT_USER_LIKE_REDIS_KEY_PREFIX + userId;
    }

    /**
     * 获取当前动态评论RedisKey下 当前用户 是否喜欢的RedisKey
     * @param userId 当前用户Id
     * @return RedisKey
     */
    private String getCommentUserLoveRedisKeyPrefix(Long userId) {
        return COMMENT_USER_LOVE_REDIS_KEY_PREFIX + userId;
    }


}

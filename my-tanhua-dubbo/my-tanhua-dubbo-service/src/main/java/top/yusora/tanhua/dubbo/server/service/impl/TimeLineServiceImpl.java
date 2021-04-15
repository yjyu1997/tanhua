package top.yusora.tanhua.dubbo.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.TimeLine;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Users;
import top.yusora.tanhua.dubbo.server.service.TimeLineService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author heyu
 */
@Service
@Slf4j
public class TimeLineServiceImpl implements TimeLineService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 发布动态后，异步存入所有好友的时间线表
     * 异步执行，原理：底层开一个线程去执行该方法
     * @param userId    **发布**用户ID
     * @param publishId 发布ID
     * @return CompletableFuture 异步执行结果
     */
    @Override
    @Async
    public CompletableFuture<String> saveTimeLine(Long userId, ObjectId publishId) {
        try{
            //----------------查询好友列表---------------
            Query query = Query.query(Criteria.where("userId").is(userId));
            List<Users> usersList = this.mongoTemplate.find(query, Users.class);

            //构造时间线对象,主键ID不设置
             TimeLine timeLine = this.createTimeLine(userId,publishId);

            //------------------时间线写入每个好友时间线表----------------
            if (CollUtil.isEmpty(usersList)) {
                //没有好友 则无需存储 直接返回
                return CompletableFuture.completedFuture("ok");
            }

            //Todo: 线程池优化写入好友时间线表逻辑
            usersList.forEach(users -> {
                timeLine.setId(ObjectId.get());
                //写入数据
                this.mongoTemplate.save(timeLine, "quanzi_time_line_" + users.getFriendId());
            });

        }catch (Exception e){
            log.error("写入好友时间线表失败~ userId = " + userId + ", publishId = " + publishId, e);
            //TODO 事务回滚问题
            return CompletableFuture.completedFuture("error");
        }

        return CompletableFuture.completedFuture("ok");
    }

    /**
     * 构造时间线对象
     * @param userId 发布用户ID
     * @param publishId 发布ID
     * @return 时间线对象
     */
    private TimeLine createTimeLine(Long userId, ObjectId publishId){
        TimeLine timeLine = new TimeLine();
        timeLine.setDate(System.currentTimeMillis());
        timeLine.setPublishId(publishId);
        timeLine.setUserId(userId);
        return timeLine;
    }
}

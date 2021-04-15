package top.yusora.tanhua.dubbo.server.service;


import org.bson.types.ObjectId;

import java.util.concurrent.CompletableFuture;

/**
 * @author heyu
 */
public interface TimeLineService {

    /**
     * 发布动态后，异步存入所有好友的时间线表
     * @param userId **发布**用户ID
     * @param publishId 发布ID
     * @return
     */
    CompletableFuture<String> saveTimeLine(Long userId, ObjectId publishId);
}

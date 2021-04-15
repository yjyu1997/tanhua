package top.yusora.tanhua.server.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.QuanZiApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Publish;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author heyu
 */
@Service
@Slf4j
public class QuanziMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @DubboReference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    /**
     * 发布动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean publishMsg(String publishId) {
        return this.sendMsg(publishId, 1);
    }

    /**
     * 浏览动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean queryPublishMsg(String publishId) {
        return this.sendMsg(publishId, 2);
    }

    /**
     * 点赞动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean likePublishMsg(String publishId) {
        return this.sendMsg(publishId, 3);
    }

    /**
     * 取消点赞动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean disLikePublishMsg(String publishId) {
        return this.sendMsg(publishId, 6);
    }

    /**
     * 喜欢动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean lovePublishMsg(String publishId) {
        return this.sendMsg(publishId, 4);
    }

    /**
     * 取消喜欢动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean disLovePublishMsg(String publishId) {
        return this.sendMsg(publishId, 7);
    }

    /**
     * 评论动态消息
     *
     * @param publishId 动态Id
     * @return 是否发送成功
     */
    public Boolean commentPublishMsg(String publishId) {
        return this.sendMsg(publishId, 5);
    }

    /**
     * 发送圈子操作相关的消息
     *
     * @param publishId 动态Id
     * @param type      1-发动态，2-浏览动态， 3-点赞， 4-喜欢， 5-评论，6-取消点赞，7-取消喜欢
     * @return 是否发送成功
     */
    private Boolean sendMsg(String publishId, Integer type) {
        try {
            Long userId = UserThreadLocal.get();

            Publish publish = this.quanZiApi.queryPublishById(publishId);

            //构建消息
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", userId);
            msg.put("date", System.currentTimeMillis());
            msg.put("publishId", publishId);
            msg.put("pid", publish.getPid());
            msg.put("type", type);

            this.rocketMQTemplate.convertAndSend("tanhua-quanzi", msg);
        } catch (Exception e) {
            log.error("发送消息失败! publishId = " + publishId + ", type = " + type, e);
            return false;
        }

        return true;
    }
}
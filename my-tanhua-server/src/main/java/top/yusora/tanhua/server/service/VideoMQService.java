package top.yusora.tanhua.server.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.VideoApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Video;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author heyu
 */
@Service
@Slf4j
public class VideoMQService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @DubboReference(version = "1.0.0")
    private VideoApi videoApi;

    /**
     * 发布小视频消息
     * @param videoId 小视频id
     * @return 是否发送成功
     */
    public Boolean videoMsg(String videoId) {
        return this.sendMsg(videoId, 1);
    }

    /**
     * 点赞小视频
     * @param videoId 小视频id
     * @return 是否发送成功
     */
    public Boolean likeVideoMsg(String videoId) {
        return this.sendMsg(videoId, 2);
    }

    /**
     * 取消点赞小视频
     * @param videoId 小视频id
     * @return 是否发送成功
     */
    public Boolean disLikeVideoMsg(String videoId) {
        return this.sendMsg(videoId, 3);
    }

    /**
     * 评论小视频
     * @param videoId 小视频id
     * @return 是否发送成功
     */
    public Boolean commentVideoMsg(String videoId) {
        return this.sendMsg(videoId, 4);
    }

    /**
     * 发送小视频操作相关的消息
     *
     * @param videoId 小视频id
     * @param type     1-发动态，2-点赞， 3-取消点赞，4-评论
     * @return 是否发送成功
     */
    private Boolean sendMsg(String videoId, Integer type) {
        try {
            Long userId = UserThreadLocal.get();

            Video video = this.videoApi.queryVideoById(videoId);

            //构建消息
            Map<String, Object> msg = new HashMap<>();
            msg.put("userId", userId);
            msg.put("date", System.currentTimeMillis());
            msg.put("videoId", videoId);
            msg.put("vid", video.getVid());
            msg.put("type", type);

            this.rocketMQTemplate.convertAndSend("tanhua-video", msg);
        } catch (Exception e) {
            log.error("发送消息失败! videoId = " + videoId + ", type = " + type, e);
            return false;
        }

        return true;
    }
}
package top.yusora.tanhua.recommend.msg;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Publish;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendQuanZi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendVideo;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author heyu
 */
@Component
@RocketMQMessageListener(topic = "tanhua-video",
        consumerGroup = "tanhua-video-consumer")
@Slf4j
public class VideoMsgConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final Map<Integer, Supplier<Double>> actionMap = new HashMap<>(7);

    @PostConstruct
    public void init(){

        //1-发动态，2-点赞， 3-取消点赞，4-评论

        actionMap.put(1,() -> 2d);

        actionMap.put(2,() -> 5d);

        actionMap.put(3,() -> -5d);

        actionMap.put(4,() -> 10d);

    }

    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("接收到小视频操作消息");
        try {
            //1.解析消息内容
            String msg = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONUtil.parseObj(msg);

            Long userId = jsonObject.getLong("userId");
            Long vid = jsonObject.getLong("vid");
            Integer type = jsonObject.getInt("type");
            String videoId = jsonObject.getStr("videoId");

            //1-发动态，2-点赞， 3-取消点赞，4-评论
            RecommendVideo recommendVideo = new RecommendVideo();
            recommendVideo.setUserId(userId);
            recommendVideo.setId(ObjectId.get());
            recommendVideo.setDate(System.currentTimeMillis());
            recommendVideo.setVideoId(vid);

            if(ObjectUtil.isEmpty(type) || type<0 || type > 4){
                recommendVideo.setScore(0d);
            }
            else {
                recommendVideo.setScore(this.actionMap.get(type).get());
            }

            //数据保存到MongoDB中
            this.mongoTemplate.save(recommendVideo);
        }catch (Exception e) {
            log.error("处理消息出错！msg = " + messageExt, e);
        }
    }
}
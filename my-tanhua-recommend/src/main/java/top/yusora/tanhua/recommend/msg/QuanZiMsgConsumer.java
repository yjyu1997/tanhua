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

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author heyu
 */
@Component
@RocketMQMessageListener(topic = "tanhua-quanzi",
        consumerGroup = "tanhua-quanzi-consumer")
@Slf4j
public class QuanZiMsgConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final Map<Integer, Function<String,Double>> actionMap = new HashMap<>(7);

    @PostConstruct
    public void init(){

        //1-发动态，2-浏览动态， 3-点赞， 4-喜欢， 5-评论，6-取消点赞，7-取消喜欢
        actionMap.put(1,publishId -> {
            Publish publish = this.mongoTemplate.findById(new ObjectId(publishId), Publish.class);
            double score = 0d;
            if (ObjectUtil.isNotNull(publish)) {

                //获取图片数
                score += CollUtil.size(publish.getMedias());

                //获取文本的长度
                //文字长度：50以内1分，50~100之间2分，100以上3分
                int length = StrUtil.length(publish.getText());
                if (length >= 0 && length < 50) {
                    score += 1;
                } else if (length < 100) {
                    score += 2;
                } else {
                    score += 3;
                }
            }
            return score;
        });


        actionMap.put(2,publishId -> 1d);

        actionMap.put(3,publishId -> 5d);

        actionMap.put(4,publishId -> 8d);

        actionMap.put(5,publishId -> 10d);

        actionMap.put(6,publishId -> -5d);

        actionMap.put(7,publishId -> -8d);
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("接收到圈子操作消息");
        try {
            //1.解析消息内容
            String msg = new String(messageExt.getBody(), StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONUtil.parseObj(msg);

            Long userId = jsonObject.getLong("userId");
            Long date = jsonObject.getLong("date");
            String publishId = jsonObject.getStr("publishId");
            Long pid = jsonObject.getLong("pid");
            Integer type = jsonObject.getInt("type");


            RecommendQuanZi recommendQuanZi = new RecommendQuanZi();
            recommendQuanZi.setUserId(userId);
            recommendQuanZi.setId(ObjectId.get());
            recommendQuanZi.setDate(date);
            recommendQuanZi.setPublishId(pid);

            if(ObjectUtil.isEmpty(type) || type<0 || type > 7){
                recommendQuanZi.setScore(0d);
            }
            else {
                recommendQuanZi.setScore(this.actionMap.get(type).apply(publishId));
            }

            //数据保存到MongoDB中
            this.mongoTemplate.save(recommendQuanZi);
        }catch (Exception e) {
            log.error("处理消息出错！msg = " + messageExt, e);
        }
    }
}
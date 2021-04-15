package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommend_video")
public class RecommendVideo {

    private ObjectId id;
    /**
     * @Description // 用户id
     */
    private Long userId;
    /**
     * @Description //视频id，需要转化为Long类型
     */
    private Long videoId;
    /**
     * @Description  //得分
     */
    private Double score;
    /**
     * @Description //时间戳
     */
    private Long date;
}
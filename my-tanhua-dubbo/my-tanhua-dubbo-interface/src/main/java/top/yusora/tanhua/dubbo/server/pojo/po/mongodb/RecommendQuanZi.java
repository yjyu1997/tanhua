package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommend_quanzi")
public class RecommendQuanZi {

    private ObjectId id;
    /**
     * @Description // 用户id
     */
    private Long userId;
    /**
     * @Description //动态id，需要转化为Long类型
     */
    private Long publishId;
    /**
     * @Description //得分
     */
    private Double score;
    /**
     * @Description //时间戳
     */
    private Long date;
}
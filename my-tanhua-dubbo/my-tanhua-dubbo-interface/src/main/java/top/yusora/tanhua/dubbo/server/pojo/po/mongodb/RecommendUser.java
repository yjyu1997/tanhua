package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;


/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommend_user")
public class RecommendUser implements Serializable {

    private static final long serialVersionUID = -4296017160071130962L;

    /**
     * @Description 主键id
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description 推荐的用户id
     */
    @Indexed
    private Long userId;

    /**
     * @Description  用户id
     */
    private Long toUserId;

    /**
     * @Description  推荐得分
     */
    @Indexed
    private Double score;

    /**
     * @Description 日期
     */
    private String date;
}

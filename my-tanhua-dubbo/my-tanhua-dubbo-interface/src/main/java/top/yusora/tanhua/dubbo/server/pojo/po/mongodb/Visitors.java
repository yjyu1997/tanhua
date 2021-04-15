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
@Document(collection = "visitors")
public class Visitors implements java.io.Serializable{

    private static final long serialVersionUID = 2811682148052386573L;

    /**
     * @Description 主键ID
     */
    private ObjectId id;
    /**
     * @Description 当前用户id
     */
    private Long userId;
    /**
     * @Description 来访用户id
     */
    private Long visitorUserId;
    /**
     * @Description 来源，如首页、圈子等
     */
    private String from;
    /**
     * @Description 来访时间
     */
    private Long date;

    /**
     * @Description 得分
     */
    private Double score;

}


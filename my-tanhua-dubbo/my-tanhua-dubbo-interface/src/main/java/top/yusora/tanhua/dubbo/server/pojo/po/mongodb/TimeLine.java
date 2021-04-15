package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * 时间线表，用于存储发布的数据，每一个用户一张表进行存储
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_time_line_{userId}")
public class TimeLine implements java.io.Serializable {
    private static final long serialVersionUID = 9096178416317502524L;

    /**
     * @Description 主键Id
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description 好友id
     *
     */
    private Long userId;

    /**
     * @Description 发布id
     */
    private ObjectId publishId;

    /**
     * @Description 发布的时间
     */
    private Long date;

}


package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * 相册表，用于存储自己发布的数据，每一个用户一张表进行存储
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_album_{userId}")
public class Album implements java.io.Serializable {

    private static final long serialVersionUID = 432183095092216817L;

    /**
     * @Description 主键id
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description 发布id
     */
    private ObjectId publishId;

    /**
     * @Description 发布时间
     */
    private Long created;

}

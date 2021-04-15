package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tanhua_users")
public class Users implements java.io.Serializable{

    private static final long serialVersionUID = 6003135946820874230L;

    /**
     * @Description MongoDb _id
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description 用户id
     */
    private Long userId;

    /**
     * @Description 好友id
     */
    private Long friendId;

    /**
     * @Description 时间
     */
    private Long date;

}



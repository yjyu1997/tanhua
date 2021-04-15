package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 关注用户
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follow_user")
public class FollowUser implements java.io.Serializable {

    private static final long serialVersionUID = 3148619072405056052L;

    /**
     * @Description //主键id
     */
    private ObjectId id;
    /**
     * @Description //用户id
     */
    private Long userId;
    /**
     * @Description //关注的用户id
     */
    private Long followUserId;
    /**
     * @Description //关注时间
     */
    private Long created;
}


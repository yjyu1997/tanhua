package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 用户喜欢
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_like")
public class UserLike implements java.io.Serializable {

    private static final long serialVersionUID = 6739966698394686523L;

    private ObjectId id;
    /**
     * @Description 用户id，自己
     */
    @Indexed
    private Long userId;
    /**
     * @Description 喜欢的用户id，对方
     */
    @Indexed
    private Long likeUserId;
    /**
     * @Description  是否喜欢
     */
    private Boolean isLike;
    /**
     * @Description 创建时间
     */
    private Long created;
    /**
     * @Description 更新时间
     */
    private Long updated;

}
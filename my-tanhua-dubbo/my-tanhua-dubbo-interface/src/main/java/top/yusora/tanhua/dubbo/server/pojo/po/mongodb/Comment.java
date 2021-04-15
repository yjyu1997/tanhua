package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * 评论表
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_comment")
public class Comment implements java.io.Serializable{

    private static final long serialVersionUID = -291788258125767614L;

    /**
     * @Description 主键ID
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description 发布id
     */
    private ObjectId publishId;

    /**
     * @Description 评论类型，1-点赞，2-评论，3-喜欢
     */
    private Integer commentType;

    /**
     * @Description 评论内容
     */
    private String content;

    /**
     * @Description 评论人
     */
    private Long userId;

    /**
     * @Description 发布动态的用户id
     */
    private Long publishUserId;

    /**
     * 是否为父节点，默认是否
     */
    private Boolean isParent = false;

    /**
     * @Description 父节点id
     */
    private ObjectId parentId;

    /**
     * @Description 发表时间
     */
    private Long created;

}
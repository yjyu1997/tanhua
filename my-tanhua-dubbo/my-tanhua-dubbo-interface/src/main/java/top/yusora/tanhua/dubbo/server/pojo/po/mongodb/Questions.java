package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.yusora.tanhua.dubbo.server.anno.CascadeSave;

import java.io.Serializable;
import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "questions")
public class Questions implements Serializable {
    private static final long serialVersionUID = 7655927847670456836L;
    /**
     * @Description 主键id
     */
    @MongoId
    private ObjectId id;

    private String question;

    @DBRef
    @CascadeSave
    private List<Option> options;
}

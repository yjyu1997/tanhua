package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "questionnaire")
public class Questionnaire implements Serializable {
    private static final long serialVersionUID = 6616525383948945175L;
    /**
     * @Description 主键id
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description 问卷名称
     */
    private String name;


    private String level;

    private String cover;

    private Integer star;

    @DBRef
    private List<Questions> questions;


}

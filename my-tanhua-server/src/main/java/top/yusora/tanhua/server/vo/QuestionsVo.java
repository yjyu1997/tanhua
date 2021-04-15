package top.yusora.tanhua.server.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import top.yusora.tanhua.dubbo.server.anno.CascadeSave;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Option;

import java.io.Serializable;
import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class QuestionsVo implements Serializable {
    private static final long serialVersionUID = -4051089450493979628L;
    /**
     * @Description 主键id
     */
    private String id;

    private String question;


    private List<OptionVo> options;
}

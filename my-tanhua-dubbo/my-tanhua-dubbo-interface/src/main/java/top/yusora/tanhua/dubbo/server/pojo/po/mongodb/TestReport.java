package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_report")
public class TestReport implements Serializable {

    private static final long serialVersionUID = -5937628009093437470L;
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private Long userId;

    private ObjectId questionnaireId;

    private ObjectId resultId;

    private Long created;

    private Long updated;
}

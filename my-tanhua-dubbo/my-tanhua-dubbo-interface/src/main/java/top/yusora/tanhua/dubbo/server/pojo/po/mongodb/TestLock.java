package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_lock")
public class TestLock implements Serializable {

    private static final long serialVersionUID = 8676020800361847570L;
    @Id
    private ObjectId id;

    private Long userId;

    private ObjectId questionnaireId;

    private Boolean isLock;
}

package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "test_result")
public class TestResult implements Serializable {

    private static final long serialVersionUID = 94260487736624368L;
    @Id
    private ObjectId id;

    private String conclusion;

    private String cover;

    private List<Dimension> dimensions;
}

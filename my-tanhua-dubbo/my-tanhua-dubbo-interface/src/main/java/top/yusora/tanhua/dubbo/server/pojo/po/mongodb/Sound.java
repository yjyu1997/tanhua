package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sound")
public class Sound implements java.io.Serializable {


    private static final long serialVersionUID = 5202115200917264474L;
    /**
     * @Description 主键id
     */
    private ObjectId id;
    /**
     * @Description 自增长id
     */
    private Long sid;
    /**
     * @Description 用户Id
     */
    private Long userId;

    /**
     * @Description 视频文件
     */
    private String soundUrl;
    /**
     * @Description 创建时间
     */
    private Long created;


}


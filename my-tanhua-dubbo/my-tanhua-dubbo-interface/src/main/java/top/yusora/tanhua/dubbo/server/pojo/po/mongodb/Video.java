package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video")
public class Video implements java.io.Serializable {

    private static final long serialVersionUID = -3136732836884933873L;

    /**
     * @Description 主键id
     */
    private ObjectId id;
    /**
     * @Description 自增长id
     */
    private Long vid;
    /**
     * @Description 用户Id
     */
    private Long userId;
    /**
     * @Description 文字
     */
    private String text;
    /**
     * @Description 视频封面文件
     */
    private String picUrl;
    /**
     * @Description 视频文件
     */
    private String videoUrl;
    /**
     * @Description 创建时间
     */
    private Long created;
    /**
     * @Description 谁可以看，1-公开，2-私密，3-部分可见，4-不给谁看
     */
    private Integer seeType;
    /**
     * @Description 部分可见的列表
     */
    private List<Long> seeList;
    /**
     * @Description 不给谁看的列表
     */
    private List<Long> notSeeList;
    /**
     * @Description 经度
     */
    private String longitude;
    /**
     * @Description 纬度
     */
    private String latitude;
    /**
     * @Description 位置名称
     */
    private String locationName;
}


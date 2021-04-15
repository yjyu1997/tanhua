package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

/**
 * 发布表，动态内容
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_publish")
public class Publish implements java.io.Serializable {

    private static final long serialVersionUID = 8732308321082804771L;

    /**
     * @Description  主键id
     */
    @MongoId
    private ObjectId id;

    /**
     * @Description  发布id
     */
    private Long pid;

    /**
     * @Description  发布用户id
     */
    private Long userId;

    /**
     * @Description  文字
     */
    private String text;

    /**
     * @Description  媒体数据，图片或小视频 url
     */
    private List<String> medias;

    /**
     * @Description   谁可以看，1-公开，2-私密，3-部分可见，4-不给谁看
     */
    private Integer seeType;

    /**
     * @Description  部分可见的列表
     */
    private List<Long> seeList;

    /**
     * @Description  不给谁看的列表
     */
    private List<Long> notSeeList;

    /**
     * @Description  经度
     */
    private String longitude;

    /**
     * @Description  纬度
     */
    private String latitude;

    /**
     * @Description  位置名称
     */
    private String locationName;

    /**
     * @Description  发布时间
     */
    private Long created; 

}

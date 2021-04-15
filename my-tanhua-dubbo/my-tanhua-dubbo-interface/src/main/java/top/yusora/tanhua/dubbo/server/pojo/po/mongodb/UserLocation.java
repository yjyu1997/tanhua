package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_location")
@CompoundIndex(name = "location_index", def = "{'location': '2dsphere'}")
public class UserLocation implements java.io.Serializable{

    private static final long serialVersionUID = 4508868382007529970L;

    @MongoId
    private ObjectId id;
    /**
     * @Description  用户id
     */
    @Indexed
    private Long userId;
    /**
     * @Description //x:经度 y:纬度
     */
    private GeoJsonPoint location;
    /**
     * @Description //位置描述
     */
    private String address;
    /**
     * @Description //创建时间
     */
    private Long created;
    /**
     * @Description 更新时间
     */
    private Long updated;
    /**
     * @Description 上次更新时间
     */
    private Long lastUpdated;

}


package top.yusora.tanhua.dubbo.server.pojo.dto;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.UserLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserLocation不能序列化，所以要再定义UserLocationDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationDto implements java.io.Serializable {

    private static final long serialVersionUID = 4133419501260037769L;

    /**
     * @Description //用户id
     */
    private Long userId;
    /**
     * @Description //经度
     */
    private Double longitude;
    /**
     * @Description //维度
     */
    private Double latitude;
    /**
     * @Description //位置描述
     */
    private String address;
    /**
     * @Description //创建时间
     */
    private Long created;
    /**
     * @Description //更新时间
     */
    private Long updated;
    /**
     * @Description //上次更新时间
     */
    private Long lastUpdated;

    public static UserLocationDto format(UserLocation userLocation) {
        UserLocationDto userLocationDto = BeanUtil.toBean(userLocation, UserLocationDto.class);
        userLocationDto.setLongitude(userLocation.getLocation().getX());
        userLocationDto.setLatitude(userLocation.getLocation().getY());
        return userLocationDto;
    }

    public static List<UserLocationDto> formatToList(List<UserLocation> userLocations) {
       return userLocations.stream().map(UserLocationDto::format).collect(Collectors.toList());
    }
}
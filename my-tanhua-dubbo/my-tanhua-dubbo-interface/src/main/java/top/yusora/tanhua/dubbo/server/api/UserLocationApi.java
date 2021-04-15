package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.dto.UserLocationDto;

/**
 * @author heyu
 */
public interface UserLocationApi {

    /**
     * 更新用户地理位置
     *
     * @param userId 用户id
     * @param longitude 经度
     * @param latitude 纬度
     * @param address 地址名称
     * @return 业务是否成功
     */
    Boolean updateUserLocation(Long userId, Double longitude, Double latitude, String address);

    /**
     * 查询用户地理位置
     *
     * @param userId 用户id
     * @return 用户位置
     */
    UserLocationDto queryByUserId(Long userId);

    /**
     * 根据位置搜索（分页）
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param distance  距离(米)
     * @param page      页数
     * @param pageSize  页面大小
     * @return 用户位置信息列表
     */
    PageInfo<UserLocationDto> queryUserFromLocation(Double longitude, Double latitude, Double distance, Integer page, Integer pageSize);

}
package top.yusora.tanhua.server.service;

/**
 * @author heyu
 */
public interface BaiduService {

    /**
     * 更新位置
     * @param longitude 经度
     * @param latitude 纬度
     * @param address 地址
     * @return 是否更新成功
     */
    Boolean updateLocation(Double longitude, Double latitude, String address);
}

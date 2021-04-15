package top.yusora.tanhua.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserLocationApi;
import top.yusora.tanhua.server.service.BaiduService;
import top.yusora.tanhua.utils.UserThreadLocal;

/**
 * @author heyu
 */
@Service
@Slf4j
public class BaiduServiceImpl implements BaiduService {

    @DubboReference(version = "1.0.0")
    private UserLocationApi userLocationApi;
    /**
     * 更新位置
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param address   地址
     * @return 是否更新成功
     */
    @Override
    public Boolean updateLocation(Double longitude, Double latitude, String address) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        return this.userLocationApi.updateUserLocation(userId,longitude,latitude,address);
    }
}

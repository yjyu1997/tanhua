package top.yusora.tanhua.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.BaiduService;

import java.util.Map;

@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;

    /**
     * 更新位置
     *
     * @param param 请求体入参
     */
    @PostMapping("/location")
    public Void updateLocation(@RequestBody Map<String, Object> param) {

            Double longitude = Double.valueOf(param.get("longitude").toString());
            Double latitude = Double.valueOf(param.get("latitude").toString());
            String address = param.get("addrStr").toString();

            Boolean bool = this.baiduService.updateLocation(longitude, latitude, address);
            if (bool) {
                return null;
            }
            throw CastException.cast(ErrorCode.UPDATE_USER_LOCATION_FAILED);
    }
}


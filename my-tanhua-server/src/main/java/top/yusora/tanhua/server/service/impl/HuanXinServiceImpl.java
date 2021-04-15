package top.yusora.tanhua.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.HuanXinApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.HuanXinUser;
import top.yusora.tanhua.server.service.HuanXinService;
import top.yusora.tanhua.server.vo.HuanXinUserVo;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.Optional;

/**
 * @author heyu
 */
@Service
@Slf4j
public class HuanXinServiceImpl implements HuanXinService {
    @DubboReference(version = "1.0.0")
    private HuanXinApi huanXinApi;

    /**
     * 通过dubbo服务查询环信用户
     *
     * @return 环信用户信息
     */
    @Override
    public HuanXinUserVo queryHuanXinUser() {
        Long userId = UserThreadLocal.get();
        Optional<HuanXinUser> huanXinUserOpt = Optional.ofNullable(this.huanXinApi.queryHuanXinUser(userId));
        return huanXinUserOpt.map(huanXinUser -> new HuanXinUserVo(huanXinUser.getUsername(),
                        huanXinUser.getPassword())).orElse(null);
    }
}

package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserApi;
import top.yusora.tanhua.server.service.RSAService;
import top.yusora.tanhua.server.service.UserService;
import top.yusora.tanhua.utils.JwtUtils;

import java.util.Map;

/**
 * @author heyu
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {



    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RSAService rsaService;






    /**
     * 使用公钥解析token
     *
     * @param token JWT
     * @return 解析成功返回用户id，否则返回null
     */
    @Override
    public Long checkToken(String token) {
        // 通过token解析数据
        Map<String, Object> body = JwtUtils.checkToken(token, rsaService.getPublicKey());
        if (CollUtil.isEmpty(body)) {
            return null;
        }
        return Convert.toLong(body.get("id"));
    }
}

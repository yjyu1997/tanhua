package top.yusora.tanhua.dubbo.huanxin.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.huanxin.config.HuanXinConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY = "HX_TOKEN";
    @Autowired
    private HuanXinConfig huanXinConfig;

    /**
     * 获取token，先从redis中获取，如果没有，再去环信接口获取 *
     *
     * @return token
     */
    public String getToken() {
        String token = this.redisTemplate.opsForValue().get(REDIS_KEY);
        if (StrUtil.isNotEmpty(token)) {
            return token;
        }
        return refreshToken();
    }

    /**
     * 刷新token，请求环信接口，将token存储到redis中 *
     *
     * @return 远程请求的token
     */
    public String refreshToken() {
        String url = this.huanXinConfig.getUrl() +
                this.huanXinConfig.getOrgName() + "/" +
                this.huanXinConfig.getAppName() + "/token";
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", "client_credentials");
        param.put("client_id", this.huanXinConfig.getClientId());
        param.put("client_secret", this.huanXinConfig.getClientSecret());
        HttpResponse httpResponse = HttpRequest.post(url).timeout(5000)
                //请求体
                .body(JSONUtil.toJsonStr(param))
                .execute();
        if (!httpResponse.isOk()) {
            log.error("环信刷新token失败~~~ ");
            return null;
        }
        JSONObject json = JSONUtil.parseObj(httpResponse.body());
        String token = json.getStr("access_token");
        //提前一小时失效
        long expires = json.getLong("expires_in") - 3600;
       //将token存储到redis中
        this.redisTemplate.opsForValue().set(REDIS_KEY, token, expires, TimeUnit.SECONDS);
        return token;
    }
}
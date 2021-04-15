package top.yusora.tanhua.sso;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.yusora.tanhua.sso.config.AliyunSMSConfig;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MyTanhuaSsoApplicationTests {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private AliyunSMSConfig aliyunSMSConfig;


    @Test
    void contextLoads() {
    }

    @Test
    public void testRedis(){
        this.redisTemplate.opsForValue().set("redisKey", "redisCode", Duration.ofMinutes(2));
    }

    @Test
    public void testAliyunSMS(){
        System.out.println(this.aliyunSMSConfig.getAccessKeyId());
    }

    @Test
    public void testJWT(){
        String secret = "itcast";

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("mobile", "12345789");
        claims.put("id", "2");

        // 生成token
        String jwt = Jwts.builder()
                .setClaims(claims) //设置响应数据体
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                .compact();

        System.out.println(jwt); //eyJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIxMjM0NTc4OSIsImlkIjoiMiJ9.VivsfLzrsKFOJo_BdGIf6cKY_7wr2jMOMOIGaFt_tps

        // 通过token解析数据
        Map<String, Object> body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();

        System.out.println(body); //{mobile=12345789, id=2}
    }
}

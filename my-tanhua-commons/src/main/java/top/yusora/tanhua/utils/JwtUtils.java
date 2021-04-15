package top.yusora.tanhua.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.RSA;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author heyu
 */
@Slf4j
public class JwtUtils {

    /**
     * 采用RSA加密算法生成token
     *
     * @param claims     token中存储的数据，不能放置敏感数据
     * @param privateKey 私钥字符串
     * @param time       token有效期时间，单位为：小时
     * @return
     */
    public static String createToken(Map<String, Object> claims, String privateKey, int time) {
        RSA rsa = new RSA(privateKey, null);
        Map<String, Object> header = new HashMap<>(2);
        header.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);
        header.put(JwsHeader.ALGORITHM, "RS256");

        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.RS256, rsa.getPrivateKey())
                .setExpiration(DateUtil.offsetHour(new Date(), time))
                .compact();
    }

    /**
     * 通过公钥校验token
     *
     * @param token
     * @param publicKey
     * @return 返回null说明token无效或已过期
     */
    public static Map<String, Object> checkToken(String token, String publicKey) {
        try {
            RSA rsa = new RSA(null, publicKey);
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(rsa.getPublicKey())
                    .parseClaimsJws(token)
                    .getBody();
            return body;
        } catch (ExpiredJwtException e) {
            log.error("token已经过期！ token = " + token, e);
        } catch (Exception e) {
            log.error("token不合法！ token = " + token, e);
        }
        return null;
    }

}


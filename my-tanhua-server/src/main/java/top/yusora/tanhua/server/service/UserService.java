package top.yusora.tanhua.server.service;


/**
 * @author heyu
 */
public interface UserService {

    /**
     * 使用公钥解析token
     *
     * @param token JWT
     * @return 解析成功返回用户id，否则返回null
     */
    Long checkToken(String token);
}

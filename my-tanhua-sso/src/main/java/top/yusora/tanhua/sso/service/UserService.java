package top.yusora.tanhua.sso.service;



public interface UserService {

    /**
     * 登录逻辑
     *
     * @param mobile 手机号
     * @param code 验证码
     * @return 如果校验成功返回token，失败返回null
     */
    String login(String mobile, String code);


    /**
     * 使用公钥解析token
     *
     * @param token JWT token
     * @return 解析成功返回用户id，否则返回null
     */
    Long checkToken(String token);


}

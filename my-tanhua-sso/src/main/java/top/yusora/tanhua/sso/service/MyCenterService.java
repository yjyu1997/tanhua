package top.yusora.tanhua.sso.service;

/**
 * @author heyu
 */
public interface MyCenterService {

    /**
     * 发送验证码-修改手机号
     * @param token JWT token
     * @return 是否发送成功
     */
    Boolean sendVerificationCode(String token);

    /**
     * 校验验证码
     * @param code 用户输入的验证码
     * @param token jwt token
     * @return 校验是否成功
     */
    Boolean checkVerificationCode(String code, String token);

    /**
     * 保存新手机号
     * @param token jwt token
     * @param newPhone 用户新手机号
     * @return 是否保存成功
     */
    boolean updatePhone(String token, String newPhone);
}

package top.yusora.tanhua.sso.service;

import top.yusora.tanhua.vo.ErrorResult;

public interface SmsService {

    /**
     * 短信验证码服务： 1。发送 2。存入Redis
     * @param phone 手机号
     * @return 发送结果 成功：null
     */
    ErrorResult sendCheckCode(String phone);
}

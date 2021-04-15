package top.yusora.tanhua.constant;

public enum SSOCode {
    /**
     * @Description 发送短信失败
     */
    SSO_SMS_API_REJECTED("000000","向短信平台发送失败！"),
    SSO_CHECKCODE_NOT_EXPIRED("000001","上一次发送的验证码还未失效！"),
    SSO_SEND_SMS_FAILED("000002", "短信验证码发送失败！"),
    SSO_SAVE_USER_INFO_FAILED("000001","保存用户信息失败！"),
    SSO_USER_LOGIN_FAILED("000002","用户登陆失败！"),
    SSO_SAVE_USER_LOGO_FAILED("000001","保存用户logo失败！");

    private String errCode;
    private String errMessage;

    SSOCode(String errCode, String errMessage) {
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    @Override
    public String toString() {
        return "SSOCode{" +
                "errCode='" + errCode + '\'' +
                ", errMessage='" + errMessage + '\'' +
                '}';
    }
}

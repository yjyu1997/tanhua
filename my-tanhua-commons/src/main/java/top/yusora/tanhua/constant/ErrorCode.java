package top.yusora.tanhua.constant;

/**
 * @author heyu
 */

public enum ErrorCode {
    /**
     * @Description 向短信平台发送失败！
     */
    SSO_SMS_API_REJECTED("000000","向短信平台发送失败！"),
    /**
     * @Description 上一次发送的验证码还未失效！
     */
    SSO_CHECKCODE_NOT_EXPIRED("000001","上一次发送的验证码还未失效！"),
    /**
     * @Description 短信验证码发送失败！
     */
    SSO_SEND_SMS_FAILED("000002", "短信验证码发送失败！"),
    /**
     * @Description 保存用户信息失败！
     */
    SSO_SAVE_USER_INFO_FAILED("000001","保存用户信息失败！"),
    /**
     * @Description 用户登陆失败！
     */
    SSO_USER_LOGIN_FAILED("000002","用户登陆失败！"),
    /**
     * @Description 保存用户logo失败！
     */
    SSO_SAVE_USER_LOGO_FAILED("000001","保存用户logo失败！"),

    /**
     * @Description 校验验证码失败
     */
    SSO_VALIDATE_CHECKCODE_FAILED("000002","校验验证码失败！"),

    /**
     * @Description 更新用户手机号失败
     */
    SSO_UPDATE_PHONE_FAILED("000001","更新用户手机号失败！"),

    /**
     * @Description 结果集为空
     */
    RESULT_IS_NULL("000003","调用结果为空！"),
    /**
     * @Description 保存评论失败
     */
    SAVE_COMMENTS_FAILURE("000004","保存评论失败！"),

    /**
     * @Description 点赞失败
     */
    LIKE_OPERATION_FAILED("000005","点赞失败！"),
    /**
     * @Description 取消点赞失败
     */
    DISLIKE_OPERATION_FAILED("000006","取消点赞失败！"),

    /**
     * @Description 保存评论失败
     */
    SAVE_PUBLISH_FAILED("000007","保存动态失败！"),

    /**
     * @Description 喜欢失败
     */
    LOVE_OPERATION_FAILED("000008","喜欢失败！"),
    /**
     * @Description 取消点赞失败
     */
    DISLOVE_OPERATION_FAILED("000009","取消喜欢失败！"),

    /**
     * @Description 查询动态失败
     */
    QUERY_PUBLISH_FAILED("000010","查询动态失败！"),

    /**
     * @Description 查询今日佳人失败
     */
    QUERY_TODAY_BEST_FAILED("000011","查询今日佳人失败！"),

    /**
     * @Description 查询推荐用户列表出错
     */
    QUERY_RECOMMENDED_USER_FAILED("000012","查询推荐用户列表出错! "),

    /**
     * @Description 保存视频失败
     */
    SAVE_VIDEO_FAILED("000013","保存视频失败！"),

    /**
     * @Description 拉取视频失败
     */
    QUERY_VIDEO_FAILED("000014","拉取视频失败！"),

    /**
     * @Description 关注视频用户失败
     */
    FOLLOW_USER_FAILED("000015","关注视频用户失败！"),
    /**
     * @Description 取消关注视频用户失败
     */
    UNFOLLOW_USER_FAILED("000016","取消关注视频用户失败！"),
    /**
     * @Description 环信用户不存在
     */
    HUANXIN_USER_DOESNT_EXIST("000017","环信用户不存在！请联系管理员"),
    /**
     * @Description 根据环信id查询用户信息失败
     */
    QUERY_HUANXIN_USER_INFO_FAILED("000018","根据环信id查询用户信息失败!"),
    /**
     * @Description 根据用户id查询用户信息出错
     */
    QUERY_USER_INFO_FAILED("000019","根据用户id查询用户信息出错"),
    /**
     * @Description 添加联系人失败
     */
    ADD_CONTACT_FAILED("000020","添加联系人失败!"),

    /**
     * @Description 添加联系人失败
     */
    QUERY_CONTACT_FAILED("000021","查看联系人列表失败!"),

    /**
     * @Description 查询点赞列表失败
     */
    QUERY_LIKE_LIST_FAILED("000022","查询点赞列表失败~"),
    /**
     * @Description 查询喜欢列表失败
     */
    QUERY_LOVE_LIST_FAILED("000023","查询喜欢列表失败~"),
    /**
     * @Description 查询评论列表失败
     */
    QUERY_COMMENT_LIST_FAILED("000024","查询评论列表失败~"),
    /**
     * @Description 查询公告列表失败
     */
    QUERY_ANNOUNCEMENT_FAILED("000025","查询公告列表失败~ "),

    /**
     * @Description 查询个人相册失败
     */
    QUERY_ALBUMS_FAILED("000026","查询个人相册失败～"),

    /**
     * @Description 回复问题失败
     */
    REPLY_QUESTION_FAILED("000027","回复问题失败～"),

    /**
     * @Description 更新地理位置失败
     */
    UPDATE_USER_LOCATION_FAILED("000027","更新地理位置失败~"),

    /**
     * @Description 喜欢操作失败
     */
    LIKE_USER_FAILED("000028","喜欢操作失败~"),

    /**
     * @Description 不喜欢操作失败
     */
    DISLIKE_USER_FAILED("000029","不喜欢操作失败~"),

    /**
     * @Description 更新用户信息失败
     */
    UPDATE_USER_INFO_FAILED("000030","更新用户信息失败~"),

    /**
     * @Description 查询喜欢数量失败
     */
    QUERY_LIKE_USER_COUNT_FAILED("000031","查询喜欢数量失败~"),

    /**
     * @Description 查询用户列表失败
     */
    QUERY_USER_LIKE_LIST_FAILED("000032","查询用户列表失败～ "),

    /**
     * @Description 查询用户相关设置失败
     */
    QUERY_SETTINGS_FAILED("000033","查询用户相关设置失败～"),
    /**
     * @Description 设置陌生人问题失败
     */
    SETTING_QUESTION_FAILED("000034","设置陌生人问题失败～"),

    /**
     * @Description 查询黑名单列表失败
     */
    QUERY_BLACKLIST_FAILED("000035","查询黑名单列表失败～"),
    /**
     * @Description 移除黑名单失败
     */
    DEL_BLACKLIST_FAILED("000036","移除黑名单失败～"),
    /**
     * @Description 更新通知设置失败
     */
    UPDATE_NOTIFICATION_FAILED("000037","更新通知设置失败～"),

    /**
     * @Description 发送语音失败
     */
    SEND_SOUND_FAILED("000038","发送语音失败～"),

    /**
     * @Description 获取语音失败
     */
    GET_SOUND_FAILED("000039","获取语音失败～"),
    /**
     * @Description 获取问卷列表失败
     */
    GET_QUESTIONNAIRES_FAILED("000040","获取问卷列表失败～"),

    /**
     * @Description 提交问卷失败
     */
    SUBMIT_QUESTIONNAIRES_FAILED("000041","提交问卷失败～"),

    /**
     * @Description 获取测试报告失败
     */
    GET_REPORT_FAILED("000042","获取测试报告失败～"),
    /**
     * @Description 未知错误
     */
    UNKNOWN_ERROR("500","出错了，请稍后再试~");

    /**
     * @Description 错误代码
     */
    private String code;

    /**
     * @Description 错误信息
     */
    private String msg;

    ErrorCode(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

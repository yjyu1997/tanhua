package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Settings;

public interface SettingsApi {

    /**
     * 根据用户id查询配置
     *
     * @param userId 用户id
     * @return 该用户的配置信息
     */
    Settings querySettings(Long userId);

    /**
     * 更新通知参数
     * @param userId 用户id
     * @param likeNotification   推送喜欢通知
     * @param pinglunNotification 推送评论通知
     * @param gonggaoNotification 推送公告通知
     */
    void updateNotification(Long userId, Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification);
}

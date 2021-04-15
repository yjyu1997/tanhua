package top.yusora.tanhua.server.service;

import top.yusora.tanhua.server.vo.CountsVo;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.SettingsVo;
import top.yusora.tanhua.server.vo.UserInfoVo;

/**
 * @author heyu
 */
public interface MyCenterService {
    /**
     * 根据用户id查询用户信息
     * @param userId 用户id，如果为空，表示查询当前登录人的信息
     * @return 用户基本信息数据
     */
    public UserInfoVo queryUserInfoByUserId(Long userId);

    /**
     * 是否喜欢
     *
     * @param userId 对方用户id
     * @return 是否喜欢
     */
    Boolean isLike(Long userId);

    /**
     * 更新用户信息
     *
     * @param userInfoVo 需更新的用户基本信息
     * @return 业务是否成功
     */
    Boolean updateUserInfo(UserInfoVo userInfoVo);

    /**
     * 查询 喜欢，互相关注，粉丝数量
     * @return 喜欢，互相关注，粉丝数量
     */
    CountsVo queryCounts();
    /**
     * 互相关注、我关注、粉丝、谁看过我 - 翻页列表
     *
     * @param type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @param nickname 昵称
     * @return 相应用户列表
     */
    PageResult queryLikeList(Integer type, Integer page, Integer pageSize, String nickname);


    /**
     * 取消喜欢
     *
     * @param userId 对方用户id
     *
     */
    void disLike(Long userId);

    /**
     * 关注粉丝
     *
     * @param userId 对方用户id
     *
     */
    void likeFan(Long userId);

    /**
     * 查询配置
     *
     * @return 用户配置信息
     */
    SettingsVo querySettings();

    /**
     * 设置陌生人问题
     * @param content 问题内容
     */
    void saveQuestions(String content);

    /**
     * 查询黑名单
     *
     * @param page 当前页数
     * @param pagesize 每页条数
     * @return 黑名单分页列表
     */
    PageResult queryBlacklist(Integer page, Integer pagesize);

    /**
     * 移除黑名单
     *
     * @param userId 用户id
     *
     */
    void delBlacklist(Long userId);

    /**
     * 更新通知参数
     * @param likeNotification   推送喜欢通知
     * @param pinglunNotification 推送评论通知
     * @param gonggaoNotification 推送公告通知
     */
    void updateNotification(Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification);
}

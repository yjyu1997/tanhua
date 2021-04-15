package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;

import java.util.List;

public interface UserInfoApi {

    /**
     * 完善个人信息
     *
     * @param userInfo 个人信息
     * @return 业务是否成功
     */
    Boolean save(UserInfo userInfo);

    /**
     * 更新个人信息，必须包含userId字段
     *
     * @param userInfo 个人信息
     * @return 业务是否成功
     */
    Boolean update(UserInfo userInfo);

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户个人信息
     */
    UserInfo queryByUserId(Long userId);

    /**
     * 根据用户id列表查询用户信息列表
     *
     * @param userIds 用户id列表
     * @return 用户个人信息
     */
    List<UserInfo> queryByUserIdList(List<Object> userIds);

    /**
     * 根据推荐用户内容查询列表
     *
     * @param userIds 用户id列表
     * @param age 年龄
     * @param city 城市
     * @param sex  性别
     * @return 用户基本信息列表
     */
    List<UserInfo> queryByRecommendUser(List<Object> userIds, Integer age, String city, Integer sex);

    /**
     * 模糊搜索用户昵称查询列表
     * @param userIds 用户id列表
     * @param keyword 昵称关键字
     * @return 用户基本信息列表
     */
    List<UserInfo> queryByNickname(List<Object> userIds,String keyword);

    /**
     * 根据性别查询用户信息列表
     * @param userIds 用户id列表
     * @param sex 性别
     * @return 用户信息列表
     */
    List<UserInfo> queryBySex(List<Object> userIds, SexEnum sex);

    /**
     * 根据用户id更新用户信息
     *
     * @param userInfo 需更新的用户信息
     * @return 更新是否成功
     */
    Boolean updateUserInfoByUserId(UserInfo userInfo);
}
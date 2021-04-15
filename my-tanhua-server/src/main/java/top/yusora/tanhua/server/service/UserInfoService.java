package top.yusora.tanhua.server.service;

import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.vo.RecommendUserQueryParam;


import java.util.List;
import java.util.Map;

/**
 * @author heyu
 */
public interface UserInfoService {
    /**
     * 根据用户Id从数据库中提取用户信息
     * @param userId 用户Id
     * @return 用户信息
     */
    UserInfo queryUserInfoByUserId(Long userId);

    /**
     * 提取用户信息列表
     *
     * @param userIds 用户Id列表
     * @return 用户信息列表
     */
    List<UserInfo> queryUserInfoList(List<Object> userIds) ;

    /**
     * 根据条件提取用户信息列表
     *
     * @param queryParam 查询条件
     * @param userIds 用户Id列表
     * @return 用户信息列表
     */
    List<UserInfo> queryUserInfoList(List<Object> userIds,RecommendUserQueryParam queryParam);


    /**
     * 模糊搜索用户昵称查询列表
     * @param userIds 用户id列表
     * @param keyword 昵称关键字
     * @return 用户基本信息列表
     */
    List<UserInfo> queryByNickname(List<Object> userIds,String keyword);


    /**
     * 根据用户Id列表查询出用户基本信息，并一一对应
     * @param userIds 用户Id列表
     * @return 用户基本信息map
     */
    Map<Long, UserInfo> getUserInfoMap(List<Object> userIds);

    /**
     * 根据用户Id列表与性别 查询出用户基本信息，并一一对应
     * @param userIds 用户Id列表
     * @param sex 性别枚举
     * @return 用户基本信息map
     */
    Map<Long, UserInfo> getUserInfoMap(List<Object> userIds, SexEnum sex);


    /**
     * 根据用户id更新用户信息
     * @param userInfo 需更新的用户信息
     * @return 更新是否成功
     */
    Boolean updateUserInfoByUserId(UserInfo userInfo);
}

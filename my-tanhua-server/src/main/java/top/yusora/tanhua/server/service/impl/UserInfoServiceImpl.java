package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserInfoApi;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.vo.RecommendUserQueryParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @DubboReference(version = "1.0.0")
    private UserInfoApi userInfoApi;

    /**
     * 根据用户Id从数据库中提取用户信息
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    @Override
    public UserInfo queryUserInfoByUserId(Long userId) {
        return this.userInfoApi.queryByUserId(userId);
    }

    /**
     * 根据条件提取用户信息列表
     *
     * @param userIds 用户Id列表
     * @return 用户信息列表
     */
    @Override
    public List<UserInfo> queryUserInfoList(List<Object> userIds) {
        return Optional.ofNullable(this.userInfoApi.queryByUserIdList(userIds))
                .orElse(Collections.emptyList());
    }

    /**
     * 根据条件提取用户信息列表
     *
     * @param userIds    用户Id列表
     * @param queryParam 查询条件
     * @return 用户信息列表
     */
    @Override
    public List<UserInfo> queryUserInfoList(List<Object> userIds, RecommendUserQueryParam queryParam) {
        return Optional.ofNullable(this.userInfoApi.queryByRecommendUser(userIds,queryParam.getAge(),
                queryParam.getCity(), StringUtils.equals(queryParam.getGender(), "man") ? 1 : 2))
                .orElse(Collections.emptyList());
    }

    /**
     * 模糊搜索用户昵称查询列表
     *
     * @param userIds 用户id列表
     * @param keyword 昵称关键字
     * @return 用户基本信息列表
     */
    @Override
    public List<UserInfo> queryByNickname(List<Object> userIds, String keyword) {
        return Optional.ofNullable(this.userInfoApi.queryByNickname(userIds,keyword))
                .orElse(Collections.emptyList());
    }

    /**
     * 根据用户Id列表查询出用户基本信息，并一一对应
     * @param userIds 用户Id列表
     * @return 用户基本信息map
     */
    @Override
    public Map<Long, UserInfo> getUserInfoMap(List<Object> userIds) {
        //------------------查询用户信息----------------------

        //获取用户基本信息
        List<UserInfo> userInfoList = this.queryUserInfoList(userIds);

        //转换成Map，提升填充效率
        return userInfoList.stream().collect(Collectors.toMap((UserInfo::getUserId),
                Function.identity()));
    }

    /**
     * 根据用户Id列表与性别 查询出用户基本信息，并一一对应
     * @param userIds 用户Id列表
     * @param sex 性别枚举
     * @return 用户基本信息map
     */
    @Override
    public Map<Long, UserInfo> getUserInfoMap(List<Object> userIds, SexEnum sex) {

        //------------------查询用户信息----------------------

        //获取用户基本信息
        List<UserInfo> userInfoList = Optional.ofNullable(this.userInfoApi.queryBySex(userIds,sex))
                .orElse(Collections.emptyList());

        //转换成Map，提升填充效率
        return userInfoList.stream().collect(Collectors.toMap((UserInfo::getUserId),
                Function.identity()));

    }

    /**
     * 根据用户id更新用户信息
     *
     * @param userInfo 需更新的用户信息
     * @return 更新是否成功
     */
    @Override
    public Boolean updateUserInfoByUserId(UserInfo userInfo) {
        return Optional.ofNullable(this.userInfoApi.updateUserInfoByUserId(userInfo))
                .orElse(false);
    }

}

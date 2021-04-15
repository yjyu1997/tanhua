package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserInfoApi;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.mapper.UserInfoMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;

import java.util.List;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Boolean save(UserInfo userInfo) {
        return this.userInfoMapper.insert(userInfo) == 1;
    }

    @Override
    public Boolean update(UserInfo userInfo) {
        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userInfo.getUserId());
        return this.userInfoMapper.update(userInfo, updateWrapper) > 0;
    }

    @Override
    public UserInfo queryByUserId(Long userId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.userInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public List<UserInfo> queryByUserIdList(List<Object> userIds) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        return this.userInfoMapper.selectList(queryWrapper);
    }

    /**
     * 根据推荐用户内容查询列表
     *
     * @param userIds 用户id列表
     * @param age        年龄
     * @param city       城市
     * @param sex        性别
     * @return 用户基本信息列表
     */
    @Override
    public List<UserInfo> queryByRecommendUser(List<Object> userIds, Integer age, String city, Integer sex) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);

        //-----------------------附加条件查询startPoint------------------------
        if (ObjectUtil.isNotEmpty(sex)) {
            //需要性别参数查询
            queryWrapper.eq("sex", sex);
        }

        if (StringUtils.isNotEmpty(city)) {
            //需要城市参数查询
            queryWrapper.like("city", city);
        }

        if (ObjectUtil.isNotEmpty(age)) {
            //设置年龄参数，条件：小于等于
            queryWrapper.le("age", age);
        }
        //-----------------------附加条件查询endPoint--------------------------

        return this.userInfoMapper.selectList(queryWrapper);
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
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        queryWrapper.like("nick_name", keyword);
        return this.userInfoMapper.selectList(queryWrapper);
    }


    /**
     * 根据性别查询用户信息列表
     * @param userIds 用户id列表
     * @param sex 性别
     * @return 用户信息列表
     */
    @Override
    public List<UserInfo> queryBySex(List<Object> userIds, SexEnum sex) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        queryWrapper.eq("sex", sex);
        return this.userInfoMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户id更新用户信息
     *
     * @param userInfo 需更新的用户信息
     * @return 更新是否成功
     */
    @Override
    public Boolean updateUserInfoByUserId(UserInfo userInfo) {
        return new LambdaUpdateChainWrapper<>(this.userInfoMapper)
                .eq(UserInfo::getUserId,userInfo.getUserId()).update(userInfo);
    }
}
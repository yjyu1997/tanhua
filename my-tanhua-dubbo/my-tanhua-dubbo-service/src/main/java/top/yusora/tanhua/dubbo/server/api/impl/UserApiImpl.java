package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserApi;
import top.yusora.tanhua.dubbo.server.mapper.UserMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.User;

@DubboService(version = "1.0.0")
@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User queryByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        return this.userMapper.selectOne(queryWrapper);
    }

    @Override
    public User queryById(Long id) {
        return this.userMapper.selectById(id);
    }

    @Override
    public Long save(String mobile) {
        User user = new User();
        user.setMobile(mobile);
        //默认密码
        user.setPassword(SecureUtil.md5("123456"));
        if (this.userMapper.insert(user) > 0) {
            return user.getId();
        }
        return null;
    }

    /**
     * 保存新手机号
     *
     * @param userId   用户id
     * @param newPhone 用户新手机号
     * @return 是否保存成功
     */
    @Override
    public Boolean updatePhone(Long userId, String newPhone) {
        //先查询新手机号是否已经注册，如果已经注册，就不能修改
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", newPhone);
        User user = this.userMapper.selectOne(queryWrapper);
        if(ObjectUtil.isNotEmpty(user)){
            //新手机号已经被注册
            return false;
        }

        user = new User();
        user.setId(userId);
        user.setMobile(newPhone);

        return this.userMapper.updateById(user) > 0;
    }
}


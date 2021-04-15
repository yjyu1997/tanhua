package top.yusora.tanhua.sso.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.UserApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.User;
import top.yusora.tanhua.sso.service.MyCenterService;
import top.yusora.tanhua.sso.service.SmsService;
import top.yusora.tanhua.sso.service.UserService;
import top.yusora.tanhua.vo.ErrorResult;

import java.util.Optional;

@Service
@Slf4j
public class MyCenterServiceImpl implements MyCenterService {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference(version = "1.0.0")
    private UserApi userApi;


    /**
     * 发送验证码-修改手机号
     *
     * @param token JWT token
     * @return 是否发送成功
     */
    @Override
    public Boolean sendVerificationCode(String token) {
        Long userId = this.userService.checkToken(token);
        if(ObjectUtil.isEmpty(userId)){
            return false;
        }

        User user = this.userApi.queryById(userId);
        if(ObjectUtil.isEmpty(user)){
            return false;
        }
        ErrorResult errorResult = this.smsService.sendCheckCode(user.getMobile());

        return errorResult == null;
    }

    /**
     * 校验验证码
     * @param code 用户输入的验证码
     * @param token jwt token
     * @return 校验是否成功
     */
    @Override
    public Boolean checkVerificationCode(String code, String token) {
        Long userId = this.userService.checkToken(token);
        if(ObjectUtil.isEmpty(userId)){
            return false;
        }

        User user = this.userApi.queryById(userId);
        if(ObjectUtil.isEmpty(user)){
            return false;
        }

        //校验验证码，先查询redis中的验证码
        String redisKey = "CHECK_CODE_" + user.getMobile();
        String value = this.redisTemplate.opsForValue().get(redisKey);


        if(StrUtil.equals(code, value)){
            //将验证码删除
            this.redisTemplate.delete(redisKey);
            return true;
        }

        return false;

    }

    /**
     * 保存新手机号
     *
     * @param token    jwt token
     * @param newPhone 用户新手机号
     * @return 是否保存成功
     */
    @Override
    public boolean updatePhone(String token, String newPhone) {
        Long userId = this.userService.checkToken(token);
        if(ObjectUtil.isEmpty(userId)){
            return false;
        }

        return Optional.ofNullable(this.userApi.updatePhone(userId, newPhone))
                .orElse(false);
    }
}

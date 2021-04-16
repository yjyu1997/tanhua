package top.yusora.tanhua.sso.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.HuanXinApi;
import top.yusora.tanhua.dubbo.server.api.TestSoulApi;
import top.yusora.tanhua.dubbo.server.api.UserApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.User;
import top.yusora.tanhua.sso.service.RSAService;
import top.yusora.tanhua.sso.service.UserService;
import top.yusora.tanhua.utils.JwtUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author heyu
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @DubboReference(version = "1.0.0")
    private UserApi userApi;

    @DubboReference(version = "1.0.0")
    private HuanXinApi huanXinApi;

    @DubboReference(version = "1.0.0")
    private TestSoulApi testSoulApi;

    @Autowired
    private RSAService rsaService;

    /**
     * 登录逻辑
     *
     * @param mobile 手机号
     * @param code 验证码
     * @return 如果校验成功返回token，失败返回null
     */
    @Override
    public String login(String mobile, String code) {
        //校验验证码是否正确
        String redisKey = "CHECK_CODE_" + mobile;

        //获取缓存中验证码
        String value = this.redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(value)) {
            //验证码失效
            return null;
        }

        if (!StringUtils.equals(value, code)) {
            // 验证码输入错误
            return null;
        }
        //验证码在校验完成后，需要废弃
        this.redisTemplate.delete(redisKey);

        //默认是已注册
        boolean isNew = false;

        //校验该手机号是否已经注册，如果没有注册，需要注册一个账号，如果已经注册，直接登录
        User user = this.userApi.queryByMobile(mobile);
        if (null == user) {
            // 该手机号未注册
            Long userId = this.userApi.save(mobile);
            user = new User();
            user.setId(userId);
            isNew = true;

            //注册环信用户
            Boolean result = this.huanXinApi.register(user.getId());
            if (!result) {
                //注册环信失败，记录日志
                log.error("注册环信用户失败~ userId = " + user.getId());
            }

            //开放默认初级问卷
            Boolean initTest = this.testSoulApi.initTestLock(user.getId());
            if(!initTest) {
                log.error("初始化问卷失败~ userId = " + user.getId());
            }
        }

        //已有该手机号
        Map<String, Object> claims = new HashMap<String, Object>(2);
        claims.put("id", user.getId());

        // 根据用户ID生成token
        String token = JwtUtils.createToken(claims, rsaService.getPrivateKey(), 12);


        //mock
         log.debug("token：{}",token);


        try {
            //发送消息通知其他系统
            Map<String, Object> msg = new HashMap<>();
            msg.put("id", user.getId());
            msg.put("date", new Date());
            //topic 为 tanhua-sso-login
            this.rocketMQTemplate.convertAndSend("tanhua-sso-login", msg);
        } catch (Exception e) {
            log.error("发送消息出错", e);
        }

        return isNew + "|" + token;

    }


    /**
     * 使用公钥解析token
     *
     * @param token JWT token
     * @return 解析成功返回用户id，否则返回null
     */
    @Override
    public Long checkToken(String token) {
        // 通过token解析数据
        Map<String, Object> body = JwtUtils.checkToken(token, this.rsaService.getPublicKey());
        if (CollUtil.isEmpty(body)) {
            return null;
        }
        return Convert.toLong(body.get("id"));
    }




}

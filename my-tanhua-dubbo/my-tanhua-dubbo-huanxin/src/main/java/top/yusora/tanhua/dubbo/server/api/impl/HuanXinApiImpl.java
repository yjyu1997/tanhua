package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.huanxin.config.HuanXinConfig;
import top.yusora.tanhua.dubbo.huanxin.service.RequestService;
import top.yusora.tanhua.dubbo.huanxin.service.TokenService;
import top.yusora.tanhua.dubbo.server.api.HuanXinApi;
import top.yusora.tanhua.dubbo.server.enums.HuanXinMessageType;
import top.yusora.tanhua.dubbo.server.mapper.HuanXinUserMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.HuanXinUser;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Slf4j
@Service
public class HuanXinApiImpl implements HuanXinApi {

    @Autowired
    private TokenService tokenService;


    @Autowired
    private HuanXinConfig huanXinConfig;

    @Autowired
    private RequestService requestService;

    @Autowired
    private HuanXinUserMapper huanXinUserMapper;

    @Override
    public String getToken() {
        return this.tokenService.getToken();
    }


    /**
     * 注册环信用户
     * 参见：http://docs-im.easemob.com/im/server/ready/user#%E6%B3%A8%E5%86%8C%E5%8D%95%E4%B8%AA%E7%94%A8%E6%88%B7_%E5%BC%80%E6%94%BE
     *
     * @param userId 用户id
     * @return 注册是否成功
     */
    @Override
    public Boolean register(Long userId) {
        String targetUrl = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/" +
                this.huanXinConfig.getAppName() + "/users";

        //init
        HuanXinUser huanXinUser = new HuanXinUser();
        huanXinUser.setUsername("HX_" + userId);
        //密码随机生成
        huanXinUser.setPassword(IdUtil.simpleUUID());
        Boolean status = Optional.ofNullable(this.requestService
                .execute(targetUrl, JSONUtil.toJsonStr(Collections.singletonList(huanXinUser)),
                        Method.POST)).map(HttpResponse::isOk).orElse(false);

        if (status) {
            //将环信的账号信息保存到数据库
            huanXinUser.setUserId(userId);
            huanXinUser.setCreated(LocalDateTime.now());
            huanXinUser.setUpdated(huanXinUser.getCreated());

            this.huanXinUserMapper.insert(huanXinUser);

            return true;
        }

        return false;
    }




    /**
     * 根据用户Id询环信账户信息
     *
     * @param userId 用户id
     * @return 环信用户对象
     */
    @Override
    public HuanXinUser queryHuanXinUser(Long userId) {
        QueryWrapper<HuanXinUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return this.huanXinUserMapper.selectOne(wrapper);
    }

    /**
     * 根据环信用户名查询用户信息
     *
     * @param userName 换信用户名
     * @return 环信用户信息 Nullable
     */
    @Override
    public HuanXinUser queryUserByUserName(String userName) {
        QueryWrapper<HuanXinUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", userName);
        return this.huanXinUserMapper.selectOne(wrapper);
    }

    /**
     * 添加好友（双向好友关系）
     * 参见：http://docs-im.easemob.com/im/server/ready/user#%E6%B7%BB%E5%8A%A0%E5%A5%BD%E5%8F%8B
     *
     * @param userId   自己的id
     * @param friendId 好友的id
     * @return 是否添加成功
     */
    @Override
    public Boolean addUserFriend(Long userId, Long friendId) {
        //校验入参
        if (!ObjectUtil.isAllNotEmpty(userId, friendId)) {
            return false;
        }
        String targetUrl = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/"
                + this.huanXinConfig.getAppName() + "/users/HX_" +
                userId + "/contacts/users/HX_" + friendId;

        try {
            // 404 -> 对方未在环信注册
            return Optional.ofNullable(this.requestService.execute(targetUrl, null, Method.POST))
                    .map(HttpResponse::isOk).orElse(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 添加失败
        return false;

    }

    /**
     * 删除好友关系（双向删除）
     * 参见：http://docs-im.easemob.com/im/server/ready/user#%E7%A7%BB%E9%99%A4%E5%A5%BD%E5%8F%8B
     *
     * @param userId   自己的id
     * @param friendId 好友的id
     * @return 是否删除成功
     */
    @Override
    public Boolean removeUserFriend(Long userId, Long friendId) {
        //校验入参
        if (!ObjectUtil.isAllNotEmpty(userId, friendId)) {
            return false;
        }

        String targetUrl = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/"
                + this.huanXinConfig.getAppName() + "/users/HX_" +
                userId + "/contacts/users/HX_" + friendId;

        try {
            // 404 -> 对方未在环信注册
            return Optional.ofNullable(this.requestService.execute(targetUrl, null, Method.DELETE))
                    .map(HttpResponse::isOk).orElse(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 删除失败
        return false;
    }

    /**
     * 以管理员身份发送消息
     * 文档地址：http://docs-im.easemob.com/im/server/basics/messages#%E5%8F%91%E9%80%81%E6%B6%88%E6%81%AF
     *
     * @param targetUserName     发送目标的用户名
     * @param huanXinMessageType 消息类型
     * @param msg                消息
     * @return 是否发送成功
     */
    @Override
    public Boolean sendMsgFromAdmin(String targetUserName, HuanXinMessageType huanXinMessageType, String msg) {
        String url = this.huanXinConfig.getUrl() + this.huanXinConfig.getOrgName()
                + "/" + this.huanXinConfig.getAppName() + "/messages";

        //消息示例：{"target_type": "users","target": ["user2","user3"],"msg": {"type": "txt","msg": "testmessage"},"from": "user1"}
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("target_type", "users");
        msgMap.put("target", Collections.singletonList(targetUserName));

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("type", huanXinMessageType.getType());
        msgData.put("msg", msg);

        msgMap.put("msg", msgData);

        //表示消息发送者;无此字段Server会默认设置为“from”:“admin”，有from字段但值为空串(“”)时请求失败
        try {
            //Todo：mock
            log.info(JSONUtil.toJsonStr(msgMap));
            return Optional.ofNullable(this.requestService.execute(url, JSONUtil.toJsonStr(msgMap), Method.POST))
                    .map(HttpResponse::isOk).orElse(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 发送失败
        return false;
    }
}


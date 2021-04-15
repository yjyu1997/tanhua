package top.yusora.tanhua.dubbo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.yusora.tanhua.dubbo.server.api.HuanXinApi;

@SpringBootTest
public class TestHuanXinApi {
    @Autowired
    private HuanXinApi huanXinApi;

    @Test
    public void testGetToken() {
        String token = this.huanXinApi.getToken();
        System.out.println(token);
    }

    @Test
    public void testRegister(){
        //注册用户id为1的用户到环信
        System.out.println(this.huanXinApi.register(1L));
    }

    @Test
    public void testQueryHuanXinUser(){
        //根据用户id查询环信用户信息
        System.out.println(this.huanXinApi.queryHuanXinUser(1L));
    }

    @Test
    public void testRegisterAllUser(){
        for (int i = 1; i < 100; i++) {
            this.huanXinApi.register(Long.valueOf(i));
        }
    }
}
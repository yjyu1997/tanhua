package top.yusora.tanhua.dubbo;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.yusora.tanhua.dubbo.huanxin.config.HuanXinConfig;
import top.yusora.tanhua.dubbo.huanxin.service.RequestService;

@SpringBootTest
public class TestRequestService {

    @Autowired
    private RequestService requestService;

    @Autowired
    private HuanXinConfig huanXinConfig;

    @Test
    public void testQueryHuanXinUser() {
        String targetUrl = this.huanXinConfig.getUrl()
                + this.huanXinConfig.getOrgName() + "/"
                + this.huanXinConfig.getAppName() + "/users/1";
        HttpResponse response = this.requestService.execute(targetUrl, null, Method.GET);
        System.out.println(response);

    }
}


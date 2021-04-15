package top.yusora.tanhua.dubbo.huanxin.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.exception.UnauthorizedException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * 环信接口通用请求服务
 */
@Service
@Slf4j
public class RequestService {

    @Autowired
    private TokenService tokenService;

    private static final int UNAUTHORIZED_CODE = 401;

    private final Map<Method, Function<String,HttpRequest>> actionMap = new HashMap<>();

    @PostConstruct
    public void init(){
        this.actionMap.put(Method.POST, HttpRequest::post);
        this.actionMap.put(Method.DELETE, HttpRequest::delete);
        this.actionMap.put(Method.GET,HttpRequest::get);
        this.actionMap.put(Method.PUT,HttpRequest::put);
    }

    /**
     * 通用的发送请求方法
     *
     * @param url    请求地址
     * @param body   请求参数
     * @param method 请求方法
     * @return http响应
     */
    @Retryable(value = UnauthorizedException.class, maxAttempts = 5, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public HttpResponse execute(String url, String body, Method method) {
        String token = this.tokenService.getToken();

        HttpRequest httpRequest;

        httpRequest = Optional.ofNullable(actionMap.get(method))
                .map((function -> function.apply(url)))
                .orElse(null);
        if(ObjectUtil.isNull(httpRequest)){
            return null;
        }

        HttpResponse response = httpRequest
                //设置请求头：内容类型Json
                .header("Content-Type", "application/json")
                //设置请求头：token
                .header("Authorization", "Bearer " + token)
                // 设置请求体数据
                .body(body)
                // 超时时间
                .timeout(20000)
                .execute(); // 执行请求
        if(response.getStatus() == UNAUTHORIZED_CODE){
            //token失效，重新刷新token
            this.tokenService.refreshToken();

            //抛出异常，需要进行重试
            throw new UnauthorizedException(url, body, method);
        }
        return response;
    }

    /**
     * 全部重试失败后执行
     * @param e 需重试异常
     * @return null
     */
    @Recover
    public HttpResponse recover(UnauthorizedException e) {
        log.error("获取token失败！url = " + e.getUrl() + ", body = " + e.getBody() + ", method = " + e.getMethod().toString());
        //如果重试5次后，依然不能获取到token，说明网络或账号出现了问题，只能返回null了，后续的请求将无法再执行
        return null;
    }
}

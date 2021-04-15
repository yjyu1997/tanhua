package top.yusora.tanhua.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.yusora.tanhua.server.interceptor.RedisCacheInterceptor;
import top.yusora.tanhua.server.interceptor.UserTokenInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RedisCacheInterceptor redisCacheInterceptor;

    @Autowired
    private UserTokenInterceptor userTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //考虑拦截器的顺序
        registry.addInterceptor(this.userTokenInterceptor).addPathPatterns("/**");
        registry.addInterceptor(this.redisCacheInterceptor).addPathPatterns("/**");
    }
}

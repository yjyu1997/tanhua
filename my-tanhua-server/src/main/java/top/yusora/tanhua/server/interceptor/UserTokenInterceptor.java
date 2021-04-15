package top.yusora.tanhua.server.interceptor;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.yusora.tanhua.server.service.UserService;
import top.yusora.tanhua.utils.NoAuthorization;
import top.yusora.tanhua.utils.UserThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author heyu
 */
@Slf4j
@Component
public class UserTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //校验handler是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //判断是否包含@NoAuthorization注解，如果包含，直接放行
        if (((HandlerMethod) handler).hasMethodAnnotation(NoAuthorization.class)) {
            return true;
        }

        //从请求头中获取token
        String token = request.getHeader("Authorization");
        if(StrUtil.isNotEmpty(token)){
            Long userId = this.userService.checkToken(token);
            if(userId != null){
                //token有效
                //将User对象放入到ThreadLocal中
                UserThreadLocal.set(userId);
                log.info("检验Token成功，放行请求{}", handler);
                return true;
            }
        }

        //token无效，响应状态为401 /无权限
        response.setStatus(401);
        log.error("检验Token失败，转至登陆页面,{}", handler);

        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //从ThreadLocal中移除User对象
        UserThreadLocal.remove();
    }
}


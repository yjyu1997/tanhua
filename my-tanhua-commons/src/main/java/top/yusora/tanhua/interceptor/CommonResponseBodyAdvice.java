package top.yusora.tanhua.interceptor;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.yusora.tanhua.utils.NoCommonResult;
import top.yusora.tanhua.vo.ErrorResult;

/**
 * 在项目中约定：
 * 1. 如果成功，就响应200状态码
 * 2. 如果失败，就响应500状态码
 * 3. 响应的数据直接返回，无需进行包装处理
 */
@ControllerAdvice
public class CommonResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        //所有没有包含@NoCommonResult注解的都进行处理
        return !returnType.hasMethodAnnotation(NoCommonResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType
            , @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (ObjectUtil.isEmpty(body)) {
            return body;
        }

        if (body instanceof ErrorResult) {
            // 如果返回对象为ErrorResult， 设置响应状态码为500
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            //正常情况下响应200
            response.setStatusCode(HttpStatus.OK);
        }

        return body;
    }
}


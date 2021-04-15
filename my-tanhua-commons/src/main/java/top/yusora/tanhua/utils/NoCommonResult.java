package top.yusora.tanhua.utils;

import java.lang.annotation.*;

/**
 * 默认情况下会统一处理响应，如果标记为此注解的方法不进行通用处理
 * @author heyu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoCommonResult {

}
package top.yusora.tanhua.utils;

import java.lang.annotation.*;

/**
 * @author heyu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented //标记注解
public @interface NoAuthorization {

}
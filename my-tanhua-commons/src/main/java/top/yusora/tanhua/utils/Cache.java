package top.yusora.tanhua.utils;

import java.lang.annotation.*;

/**
 * @author heyu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    /**
     * 缓存时间，默认为60秒
     * @return 缓存时间
     */
    String time() default "60";
}

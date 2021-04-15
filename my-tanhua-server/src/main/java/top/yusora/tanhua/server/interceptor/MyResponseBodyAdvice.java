package top.yusora.tanhua.server.interceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.yusora.tanhua.utils.Cache;
import top.yusora.tanhua.utils.RLockThreadLocal;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author heyu
 */
@Slf4j
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {
    @Value("${tanhua.cache.enable}")
    private Boolean enable;

    @Autowired
    private RedissonClient redissonClient;

    //@Autowired
    //ConcurrentHashMap<String, RLock> lockMap;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    /**
     * Whether this component supports the given controller method return type
     * and the selected {@code HttpMessageConverter} type.
     *
     * @param returnType    the return type
     * @param converterType the selected converter type
     * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
     * {@code false} otherwise
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        // 开关处于开启状态  是get请求  包含了@Cache注解
        return enable && returnType.hasMethodAnnotation(GetMapping.class)
                && returnType.hasMethodAnnotation(Cache.class);
    }

    /**
     * Invoked after an {@code HttpMessageConverter} is selected and just before
     * its write method is invoked.
     *
     * @param body                  the body to be written
     * @param returnType            the return type of the controller method
     * @param selectedContentType   the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request               the current request
     * @param response              the current response
     * @return the body that was passed in or a modified (possibly new) instance
     */
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType
            , @NonNull Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        //获取redisKey
        String redisKey = "";
        //获取锁对象
        Optional<RLock> lock = Optional.empty();
        try {
            redisKey = RedisCacheInterceptor.createRedisKey(((ServletServerHttpRequest) request).getServletRequest());

            lock = Optional.ofNullable(RLockThreadLocal.get());
            //获取桶对象
            RBucket<String> bucket = redissonClient.getBucket(redisKey);
            //获取Cache注解
            Optional<Cache> cache = Optional.ofNullable(returnType.getMethodAnnotation(Cache.class));

            if (null == body) {
                return null;
            }

            String redisValue = null;
            if (body instanceof String) {
                redisValue = (String) body;
            } else {
                redisValue = MAPPER.writeValueAsString(body);
            }

            log.info("数据库查询数据返回，正在放入缓存");
            bucket.set(redisValue, Long.parseLong(cache.map(Cache::time).orElse("60")),
                    TimeUnit.SECONDS);

        }catch (Exception e){
            log.error("响应体处理异常",e);
        }finally {
            log.info("当前处理器线程Id:  {}",Thread.currentThread().getId() );
            log.info("释放锁");
            lock.ifPresent(RLock::unlock);
            RLockThreadLocal.remove();
        }

        return body;

    }
}

package top.yusora.tanhua.server.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.yusora.tanhua.utils.Cache;
import top.yusora.tanhua.utils.EmptyUtil;
import top.yusora.tanhua.utils.ExceptionsUtil;
import top.yusora.tanhua.utils.RLockThreadLocal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author heyu
 */
@Component
@Slf4j
public class RedisCacheInterceptor implements HandlerInterceptor {
    @Value("${tanhua.cache.enable}")
    private Boolean enable;


    @Autowired
    private RedissonClient redissonClient;

    //@Autowired
    //ConcurrentHashMap<String, RLock> lockMap;

    /**
     * @Description 分布式锁后缀
     */
    public static final String LOCK_SUFFIX = "_lock";

    /**
     * @Description 默认redis等待时间
     */
    public static final int REDIS_WAIT_TIME = 6000;

    /**
     * @Description 默认redis自动释放时间
     */
    public static final int REDIS_LEASE_TIME = 2000;


    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //缓存的全局开关的校验
        if (!enable) {
            return true;
        }

        //校验handler是否是HandlerMethod
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //判断是否为get请求
        if (!((HandlerMethod) handler).hasMethodAnnotation(GetMapping.class)) {
            return true;
        }

        //判断是否添加了@Cache注解
        if (!((HandlerMethod) handler).hasMethodAnnotation(Cache.class)) {
            return true;
        }

        log.info("当前拦截器线程Id:  {}",Thread.currentThread().getId() );

        String redisKey = createRedisKey(request);
        //1、限流：为1秒时间内，同一个请求地址，同一个人，相同请求参数能访问10次
        this.tryRateLimiter(redisKey);
        //2、构建桶位
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        //3、缓存中获得对象
        String cacheData = bucket.get();
        //--------------------------------缓存未命中------------------------------------
        if (EmptyUtil.isNullOrEmpty(cacheData)) {
            //5、构建锁桶位
            RLock lock = redissonClient.getLock(redisKey + LOCK_SUFFIX);

            try {
                //6、锁等待时间为3秒，默认释放时间1秒
                if (lock.tryLock(REDIS_WAIT_TIME, REDIS_LEASE_TIME, TimeUnit.MILLISECONDS)) {
                    //7、并发情况下，在3秒内阻塞的线程在拿到锁后会再次从缓存中复查获得对象，最大可能性防止非空返回
                    cacheData = bucket.get();
                    if (!EmptyUtil.isNullOrEmpty(cacheData)) {
                        log.info("等待线程:对象返回:{}", cacheData);
                        // 将data数据进行响应
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=utf-8");
                        response.getWriter().write(cacheData);

                        return false;
                    }
                    log.info("加锁，放过第一个请求");
                    //将Rlock放入map
                    //lockMap.put(redisKey,lock);

                    RLockThreadLocal.set(lock);
                    //8、执行默认方法，查询数据库
                    return true;
                }else{
                    //12、并发情况下，等待3秒超时的线程会再次从缓存中获得对象，最大可能性防止非空返回
                    cacheData = bucket.get();
                    if (!EmptyUtil.isNullOrEmpty(cacheData)){
                        log.info("超时线程:对象返回:{}",cacheData);
                        // 将data数据进行响应
                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json; charset=utf-8");
                        response.getWriter().write(cacheData);

                        return false;
                    }
                }
            }catch (Exception e){
                //10、如果执行业务体发生异常则返回空对象
                log.error("业务执行异常：{}", ExceptionsUtil.getStackTraceAsString(e));
                log.error("发现异常，快速释放锁，防止阻塞");
                lock.unlock();
                //lockMap.remove(redisKey);
                RLockThreadLocal.remove();
                return false;
            }
        }
        //---------------------------------缓存命中------------------------------------------

        // 将data数据进行响应
        log.info("从缓存中取数据");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(cacheData);

        return false;

    }



    public void tryRateLimiter(String redisKey) throws JsonProcessingException {



        int rate = 10;

        //keyHandler的值：SERVER_CACHE_DATA_b777sadad777sd_rate_limiter
        String keyHandler = redisKey +"_rate_limiter";
        // 初始化
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(keyHandler);
        // //设置访问速率，var2为访问数，var3为单位时间，var4为时间单位 最大流速 = 每1秒钟产生5/2000个令牌
        rateLimiter.trySetRate(RateType.OVERALL,
                rate, 1, RateIntervalUnit.SECONDS);
        //从这个RateLimiter获得一个许可，阻塞直到有一个可用
        rateLimiter.acquire();
    }

    public static String createRedisKey(HttpServletRequest request) throws Exception {
        String url = request.getRequestURI();
        String param = MAPPER.writeValueAsString(request.getParameterMap());
        log.info(param);
        String token = request.getHeader("Authorization");

        String data = url + "_" + param + "_" + token;
        return "SERVER_CACHE_DATA_" + DigestUtils.md5Hex(data);
    }
}

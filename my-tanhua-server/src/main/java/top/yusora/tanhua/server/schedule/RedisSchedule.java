package top.yusora.tanhua.server.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author heyu
 */
@Component
@Slf4j
public class RedisSchedule {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private ThreadPoolTaskExecutor executorService;

    private static final String REDIS_KEY_PREFIX ="PEACH_BLOSSOM_*";


    @Scheduled(cron = "59 59 23 * * ? ")
    public void timingTest(){
        //模糊查询开头为map的所有key值
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX);
        //循环查到的所有的key
        if(keys!=null&&keys.size()>0){
            for(String key : keys){
                executorService.execute(() -> {
                    log.info(key + "正在被删除。。。  ——————>  当前线程：" + Thread.currentThread().getName() +
                            "执行定时任务的时间是："+ LocalDateTime.now());

                    //删除
                    redisTemplate.delete(key);
                });

            }
        }else{
            System.out.println("没有该类型缓存");
        }
//        System.out.println("执行定时任务的时间是："+new Date());
    }

}

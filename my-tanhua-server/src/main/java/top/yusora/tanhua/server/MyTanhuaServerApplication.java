package top.yusora.tanhua.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author heyu
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}
,scanBasePackages = "top.yusora.tanhua")
@EnableScheduling
public class MyTanhuaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyTanhuaServerApplication.class, args);
    }


    @Bean(name = "executorService")
    public ThreadPoolTaskExecutor getThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);

        executor.setMaxPoolSize(8);

        executor.setQueueCapacity(100);

        executor.setKeepAliveSeconds(60);

        executor.setThreadNamePrefix("MessageSender-Pool");

        //CallerRunsPolicy：用于被拒绝任务的处理程序，它直接在execute方法的调用线程中运行被拒绝的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;

    }
}

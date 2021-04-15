package top.yusora.tanhua.dubbo.server;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author heyu
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "top.yusora.tanhua.dubbo.server.api")
@EnableAsync
@MapperScan("top.yusora.tanhua.dubbo.server.mapper")
public class DubboApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskExecutor getThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);

        executor.setMaxPoolSize(8);

        executor.setQueueCapacity(100);

        executor.setKeepAliveSeconds(60);

        executor.setThreadNamePrefix("Pool-A");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;

    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
}


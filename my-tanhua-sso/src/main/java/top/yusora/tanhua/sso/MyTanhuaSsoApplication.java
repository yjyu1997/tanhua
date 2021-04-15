package top.yusora.tanhua.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * @author heyu
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}
        ,scanBasePackages = "top.yusora.tanhua")
public class MyTanhuaSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyTanhuaSsoApplication.class, args);
    }

}

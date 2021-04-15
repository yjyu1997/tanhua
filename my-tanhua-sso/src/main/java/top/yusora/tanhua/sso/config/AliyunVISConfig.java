package top.yusora.tanhua.sso.config;


import com.aliyun.facebody20191230.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "aliyun.vis")
@Data
public class AliyunVISConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;

    @Bean
    public Client createClient() throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(this.accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(this.accessKeySecret);
        // 访问的域名
        config.endpoint = this.endpoint;
        return new Client(config);
    }
}

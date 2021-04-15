package top.yusora.tanhua.dubbo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestRetryService {

    @Autowired
    private RetryService retryService;

    @Test
    public void testRetry() {
        System.out.println(this.retryService.execute(90));
    }
}
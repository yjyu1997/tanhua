package top.yusora.tanhua.server;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import top.yusora.tanhua.server.service.RecommendUserService;
import top.yusora.tanhua.server.vo.TodayBest;

@SpringBootTest
public class TestRecommendUserApi {

    @Autowired
    private RecommendUserService recommendUserService;

    @Test
    public void testQueryTodayBest(){
        TodayBest todayBest = this.recommendUserService.queryTodayBest(1L);
        System.out.println(todayBest);
    }

}



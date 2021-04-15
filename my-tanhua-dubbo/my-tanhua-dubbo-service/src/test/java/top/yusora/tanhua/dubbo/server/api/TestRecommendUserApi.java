package top.yusora.tanhua.dubbo.server.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRecommendUserApi {

    @Autowired
    private RecommendUserApi recommendUserApi;

    @Test
    public void testQueryWithMaxScore(){
        System.out.println(this.recommendUserApi.queryWithMaxScore(1L));
        System.out.println(this.recommendUserApi.queryWithMaxScore(8L));
        System.out.println(this.recommendUserApi.queryWithMaxScore(26L));
    }

    @Test
    public void testQueryPageInfo(){
        System.out.println(this.recommendUserApi.queryPageInfo(1L,1,5));
        System.out.println(this.recommendUserApi.queryPageInfo(1L,2,5));
        System.out.println(this.recommendUserApi.queryPageInfo(1L,3,5));
    }

    @Test
    public void testQueryCardList(){
        this.recommendUserApi.queryCardList(1L, 20)
                .forEach(recommendUser -> System.out.println(recommendUser));
    }
}

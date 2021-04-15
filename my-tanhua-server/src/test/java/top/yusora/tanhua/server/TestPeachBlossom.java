package top.yusora.tanhua.server;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import top.yusora.tanhua.server.service.RecommendUserService;
import top.yusora.tanhua.server.vo.TodayBest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class TestPeachBlossom {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testQueryTodayBest(){
        Long test_increment = this.redisTemplate.opsForValue().increment("test_increment", 1);
        System.out.println(test_increment);
    }

    @Test
    public void testJson(){
        List<Long> list = new ArrayList<>();
        Collections.addAll(list,1L,2L,3L,4L,5L,6L);
        String jsonStr = JSONUtil.toJsonStr(list);
        System.out.println(jsonStr);

        List<Long> longs = JSONArray.parseArray(jsonStr, Long.TYPE);
        System.out.println(longs);
    }

}



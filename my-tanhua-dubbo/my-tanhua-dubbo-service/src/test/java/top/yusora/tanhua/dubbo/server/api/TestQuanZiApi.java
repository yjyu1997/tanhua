package top.yusora.tanhua.dubbo.server.api;

import cn.hutool.core.convert.Convert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Sound;


@SpringBootTest
public class TestQuanZiApi {

    @Autowired
    private QuanZiApi quanZiApi;

    @Autowired
    private PeachBlossomApi peachBlossomApi;

    @Test
    public void testQueryPublishList(){
        this.quanZiApi.queryPublishList(1L, 1, 2)
                .getRecords().forEach(System.out::println);
        System.out.println("------------");
        this.quanZiApi.queryPublishList(1L, 2, 2)
                .getRecords().forEach(System.out::println);
        System.out.println("------------");
        this.quanZiApi.queryPublishList(1L, 3, 2)
                .getRecords().forEach(System.out::println);
        System.out.println("----------------------测试HexString--------------------------");
        this.quanZiApi.queryPublishList(1L, 1, 2)
                .getRecords().forEach(publish -> System.out.println(publish.getId().toHexString()));
        System.out.println("----------------------测试toString--------------------------");
        this.quanZiApi.queryPublishList(1L, 1, 2)
                .getRecords().forEach(publish -> System.out.println(publish.getId().toString()));
    }

    @Test
    public void testLike(){
        Long userId = 1L;
        String publishId = "5fae53947e52992e78a3afb1";
        Boolean data = this.quanZiApi.queryUserIsLike(userId, publishId);
        System.out.println(data);

        System.out.println(this.quanZiApi.likeComment(userId, publishId, QuanZiType.PUBLISH));

        System.out.println(this.quanZiApi.queryLikeCount(publishId));

        System.out.println(this.quanZiApi.disLikeComment(userId, publishId));

        System.out.println(this.quanZiApi.queryLikeCount(publishId));
    }

    @Test
    public void testSound(){
        for (int i = 2; i < 100; i++) {

            Sound sound = new Sound();
            sound.setUserId(Convert.toLong(i));
            sound.setSoundUrl("http://192.168.1.146:8888/group1/M00/00/00/wKgBkmB0S2KAPuV3AAAmTywZrFQ871.m4a");
            peachBlossomApi.saveSound(sound);

            sound.setSoundUrl("http://192.168.1.146:8888/group1/M00/00/00/wKgBkmB0S7KAe-huAAArCvMpkNQ594.m4a");
            peachBlossomApi.saveSound(sound);

            sound.setSoundUrl("http://192.168.1.146:8888/group1/M00/00/00/wKgBkmB0S_aAAf2OAAAzn-tz7cA514.m4a");

            peachBlossomApi.saveSound(sound);
        }
    }
}

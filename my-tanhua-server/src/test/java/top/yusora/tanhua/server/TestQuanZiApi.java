package top.yusora.tanhua.server;

import cn.hutool.core.collection.ListUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import top.yusora.tanhua.dubbo.server.api.QuanZiApi;
import top.yusora.tanhua.dubbo.server.enums.CommentType;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Publish;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestQuanZiApi {

    @DubboReference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Test
    public void testSavePublish(){
        Publish publish = new Publish();
        publish.setText("测试发布动态");
        publish.setMedias(ListUtil.toList("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/6/1.jpg", "https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/6/CL-3.jpg"));
        publish.setUserId(1L);
        publish.setSeeType(1);
        publish.setLongitude("116.350426");
        publish.setLatitude("40.066355");
        publish.setLocationName("中国江苏省苏州市相城区嘉境天成");
        this.quanZiApi.savePublish(publish);
    }

    @Test
    public void testComment(){
        //构造分页查询条件

        Query query = new Query(Criteria.where("publishId").is(null)
                .and("commentType").is(CommentType.COMMENT.getType()));

        //查询评论列表
       // List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);
    }
}
package top.yusora.tanhua.dubbo.server.api;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.dubbo.server.mapper.QuestionMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Visitors;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Question;

import java.util.List;


@SpringBootTest
public class TestQuestionApi {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testQueryQuestion(){
        System.out.println(new LambdaQueryChainWrapper<>(this.questionMapper)
                .eq(Question::getUserId,1L).one());
    }

    @Test
    public void testVisitor(){
        Query query = Query.query(Criteria.where("userId").is(99L));
        Visitors visitors= this.mongoTemplate.findOne(query, Visitors.class);
        System.out.println(visitors);

    }



}

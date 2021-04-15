package top.yusora.tanhua.dubbo.server.api;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.*;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TestPeachBlossom {

    @Autowired
    private PeachBlossomApi peachBlossomApi;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestSoulApi testSoulApi;

//    @Test
//    public void testGetSound(){
//        for (int i = 0; i < 97; i++) {
//            System.out.println(this.peachBlossomApi.getSound(1L,2L+i));
//        }
//
////        System.out.println(this.peachBlossomApi.getSound(1L,1L));
//
////        for (int i = 0; i < 100; i++) {
////            System.out.println(this.peachBlossomApi.getSound(1L, 198L));
////        }
//
//    }

    @Test
    public void testCreateQuestion(){
        Option A = Option.builder().id(ObjectId.get()).option("早晨").point(2).build();
        Option B = Option.builder().id(ObjectId.get()).option("下午及傍晚").point(4).build();
        Option C = Option.builder().id(ObjectId.get()).option("夜里").point(6).build();

        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("你何时感觉最好?");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion2(){
        Option A = Option.builder().id(ObjectId.get()).option("大步地快走").point(6).build();
        Option B = Option.builder().id(ObjectId.get()).option("小步地块走").point(4).build();
        Option C = Option.builder().id(ObjectId.get()).option("不快，仰着头面对着世界").point(7).build();
        Option D = Option.builder().id(ObjectId.get()).option("不快，低着头").point(2).build();
        Option E = Option.builder().id(ObjectId.get()).option("很慢").point(1).build();

        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D,E);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("你走路时是");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion3(){

        Option A = Option.builder().id(ObjectId.get()).option("手臂交叠站着 ").point(4).build();
        Option B = Option.builder().id(ObjectId.get()).option("双手紧握着").point(2).build();
        Option C = Option.builder().id(ObjectId.get()).option("一只手或两只手放在臀部").point(5).build();
        Option D = Option.builder().id(ObjectId.get()).option("碰着或推着与你说话的人").point(7).build();
        Option E = Option.builder().id(ObjectId.get()).option("玩着你的耳朵，摸着你的下巴或用手整理头发").point(6).build();

        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D,E);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("和人说话时");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion4(){
//4、坐着休息时
//A 两膝盖并拢  B 两腿交叉 C 两腿伸直 D 一腿蜷在身下
        Option A = Option.builder().id(ObjectId.get()).option("两膝盖并拢").point(4).build();
        Option B = Option.builder().id(ObjectId.get()).option("两腿交叉").point(6).build();
        Option C = Option.builder().id(ObjectId.get()).option("两腿伸直").point(2).build();
        Option D = Option.builder().id(ObjectId.get()).option("一腿蜷在身下").point(1).build();

        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("坐着休息时");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion5(){


        Option A = Option.builder().id(ObjectId.get()).option("一个欣赏的大笑").point(6).build();
        Option B = Option.builder().id(ObjectId.get()).option("笑着，但不大声").point(4).build();
        Option C = Option.builder().id(ObjectId.get()).option("轻声地笑").point(3).build();
        Option D = Option.builder().id(ObjectId.get()).option("羞怯的微笑").point(5).build();


        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("碰到你感到发笑的事时，你的反应是");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion6(){

        Option A = Option.builder().id(ObjectId.get()).option("很大声地入场以引起注意 ").point(6).build();
        Option B = Option.builder().id(ObjectId.get()).option("安静地入场，找你认识的人").point(4).build();
        Option C = Option.builder().id(ObjectId.get()).option("非常安静地入场，尽量保持不被注意").point(2).build();

        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("当你去一个派对或社交场合时");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion7(){

        Option A = Option.builder().id(ObjectId.get()).option("欢迎他").point(6).build();
        Option B = Option.builder().id(ObjectId.get()).option("感到非常愤怒").point(2).build();
        Option C = Option.builder().id(ObjectId.get()).option("在上述两极端之间").point(4).build();


        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("当你非常专心工作时，有人打断你，你会");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }

    @Test
    public void testCreateQuestion8(){

        Option A = Option.builder().id(ObjectId.get()).option("红或橘色").point(6).build();
        Option B = Option.builder().id(ObjectId.get()).option("黑色").point(7).build();
        Option C = Option.builder().id(ObjectId.get()).option("黄色或浅蓝色").point(5).build();
        Option D = Option.builder().id(ObjectId.get()).option("绿色").point(4).build();
        Option E = Option.builder().id(ObjectId.get()).option("深蓝色或紫色").point(3).build();
        Option F = Option.builder().id(ObjectId.get()).option("白色").point(2).build();
        Option G = Option.builder().id(ObjectId.get()).option("棕色或灰色").point(1).build();

        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D,E,F,G);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("下列颜色中，你最喜欢哪一种颜色？");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }

    @Test
    public void testCreateQuestion9(){

        Option A = Option.builder().id(ObjectId.get()).option("仰躺，伸直").point(7).build();
        Option B = Option.builder().id(ObjectId.get()).option("俯躺，伸直").point(6).build();
        Option C = Option.builder().id(ObjectId.get()).option("侧躺，微蜷").point(4).build();
        Option D = Option.builder().id(ObjectId.get()).option("头睡在一手臂上").point(2).build();
        Option E = Option.builder().id(ObjectId.get()).option("被子盖过头").point(1).build();


        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D,E);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("临入睡的前几分钟，你在床上的姿势是");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testCreateQuestion10(){

        Option A = Option.builder().id(ObjectId.get()).option("落下").point(4).build();
        Option B = Option.builder().id(ObjectId.get()).option("打架或挣扎").point(2).build();
        Option C = Option.builder().id(ObjectId.get()).option("找东西或人").point(3).build();
        Option D = Option.builder().id(ObjectId.get()).option("飞或漂浮").point(5).build();
        Option E = Option.builder().id(ObjectId.get()).option("你平常不做梦").point(6).build();
        Option F = Option.builder().id(ObjectId.get()).option("你的梦都是愉快的").point(1).build();


        List<Option> options = new ArrayList<>();
        Collections.addAll(options,A,B,C,D,E,F);

        Questions question = new Questions();

        question.setId(ObjectId.get());
        question.setQuestion("你经常梦到自己在");
        question.setOptions(options);


        this.mongoTemplate.save(question);
    }


    @Test
    public void testGetQuestion(){
        String objectId = "60768594dd28834a943e8725";
        Questions q1= this.mongoTemplate.findById(new ObjectId(objectId), Questions.class);
        System.out.println(q1);
        objectId = "60768594dd28834a943e8723";
        Integer point = this.mongoTemplate.findById(new ObjectId(objectId),Option.class).getPoint();
        System.out.println(point);
//        String optionIdStr = "60766477c8be20380537e98f";
//        ObjectId optionId = new ObjectId(optionIdStr);
//        List<Option> options = q1.getOptions();
//        Integer point = 0;
//        for (Option option : options) {
//            if(optionId.equals(option.getId())){
//               point = option.getPoint();
//            }
//        }
//        System.out.println(point);
    }

    @Test
    public void testCreateQuestionnaire(){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(ObjectId.get());
        questionnaire.setName("初级灵魂题");
        questionnaire.setLevel("初级");
        questionnaire.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_01.png");
        questionnaire.setStar(2);

        List<ObjectId> list = new ArrayList<>();
        list.add(new ObjectId("6077c0da1883db2b32fdc712"));
        list.add(new ObjectId("6077c0db1883db2b32fdc718"));
        list.add(new ObjectId("6077c0db1883db2b32fdc71e"));
        list.add(new ObjectId("6077c0db1883db2b32fdc723"));
        list.add(new ObjectId("6077c0db1883db2b32fdc728"));
        list.add(new ObjectId("6077c0db1883db2b32fdc72c"));
        list.add(new ObjectId("6077c0db1883db2b32fdc730"));
        list.add(new ObjectId("6077c0db1883db2b32fdc738"));
        list.add(new ObjectId("6077c0db1883db2b32fdc73e"));
        list.add(new ObjectId("6077c0db1883db2b32fdc745"));

        Query query = Query.query(Criteria.where("id").in(list));
        List<Questions> questions = this.mongoTemplate.find(query,Questions.class);

        questionnaire.setQuestions(questions);

        this.mongoTemplate.save(questionnaire);
    }


    @Test
    public void testCreateQuestionnaire2(){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(ObjectId.get());
        questionnaire.setName("中级灵魂题");
        questionnaire.setLevel("中级");
        questionnaire.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_02.png");
        questionnaire.setStar(3);

        List<ObjectId> list = new ArrayList<>();
        list.add(new ObjectId("6077c0f0d68fb759a75659d1"));
        list.add(new ObjectId("6077c0f0d68fb759a75659d7"));
        list.add(new ObjectId("6077c0f0d68fb759a75659dd"));
        list.add(new ObjectId("6077c0f0d68fb759a75659e2"));
        list.add(new ObjectId("6077c0f0d68fb759a75659e7"));
        list.add(new ObjectId("6077c0f0d68fb759a75659eb"));
        list.add(new ObjectId("6077c0f0d68fb759a75659ef"));
        list.add(new ObjectId("6077c0f0d68fb759a75659f7"));
        list.add(new ObjectId("6077c0f0d68fb759a75659fd"));
        list.add(new ObjectId("6077c0f0d68fb759a7565a04"));
        Query query = Query.query(Criteria.where("id").in(list));
        List<Questions> questions = this.mongoTemplate.find(query,Questions.class);

        questionnaire.setQuestions(questions);

        this.mongoTemplate.save(questionnaire);
    }

    @Test
    public void testCreateQuestionnaire3(){
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(ObjectId.get());
        questionnaire.setName("高级灵魂题");
        questionnaire.setLevel("高级");
        questionnaire.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_03.png");
        questionnaire.setStar(5);

        List<ObjectId> list = new ArrayList<>();
        list.add(new ObjectId("6077c103c61ee6347903b629"));
        list.add(new ObjectId("6077c104c61ee6347903b62f"));
        list.add(new ObjectId("6077c104c61ee6347903b635"));
        list.add(new ObjectId("6077c104c61ee6347903b63a"));
        list.add(new ObjectId("6077c104c61ee6347903b63f"));
        list.add(new ObjectId("6077c104c61ee6347903b643"));
        list.add(new ObjectId("6077c104c61ee6347903b647"));
        list.add(new ObjectId("6077c104c61ee6347903b64f"));
        list.add(new ObjectId("6077c104c61ee6347903b655"));
        list.add(new ObjectId("6077c104c61ee6347903b65c"));
        Query query = Query.query(Criteria.where("id").in(list));
        List<Questions> questions = this.mongoTemplate.find(query,Questions.class);

        questionnaire.setQuestions(questions);

        this.mongoTemplate.save(questionnaire);
    }

    @Test
    public void TestCreateTestResult(){

        List<Dimension> dimensions = new ArrayList<>();
        dimensions.add(Dimension.builder().key("外向").value("80%").build());
        dimensions.add(Dimension.builder().key("自信").value("85%").build());
        dimensions.add(Dimension.builder().key("果断").value("80%").build());
        dimensions.add(Dimension.builder().key("强势").value("90% ").build());
        TestResult testResult = TestResult.builder().conclusion("狮子型：性格为充满自信、竞争心强、主动且企图心强烈，是个有决断力的领导者。一般而言，狮子型的人胸怀大志，勇于冒险，看问题能够直指核心，并对目标全力以赴。他们在领导风格及决策上，强调权威与果断，擅长危机处理，此种性格最适合开创性与改革性的工作。")
                .cover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/lion.png").dimensions(dimensions).id(ObjectId.get()).build();

        this.mongoTemplate.save(testResult);

    }

    @Test
    public void testCreateTestLock(){
        for (int i = 1; i < 100 ; i++) {
            TestLock lock = new TestLock();
            lock.setId(ObjectId.get());
            lock.setIsLock(false);
            lock.setUserId(Convert.toLong(i));
            lock.setQuestionnaireId(new ObjectId("6077c2b106d34e212babc3fc"));
            this.mongoTemplate.save(lock);
        }

        for (int i = 1; i < 100 ; i++) {
            TestLock lock = new TestLock();
            lock.setId(ObjectId.get());
            lock.setIsLock(true);
            lock.setUserId(Convert.toLong(i));
            lock.setQuestionnaireId(new ObjectId("6077c2c46ccd0a4c4b19919e"));
            this.mongoTemplate.save(lock);
        }

        for (int i = 1; i < 100 ; i++) {
            TestLock lock = new TestLock();
            lock.setId(ObjectId.get());
            lock.setIsLock(true);
            lock.setUserId(Convert.toLong(i));
            lock.setQuestionnaireId(new ObjectId("6077c2d5b99694606124a736"));
            this.mongoTemplate.save(lock);
        }
    }


    @Test
    public void testGetQuestionnaire(){
        System.out.println(this.testSoulApi.getQuestionnaires());
    }


    @Test
    public void getQuestionnaireByQuestion(){
        List<ObjectId> list = new ArrayList<>();
        list.add(new ObjectId("6077c103c61ee6347903b629"));
        list.add(new ObjectId("6077c104c61ee6347903b62f"));
        list.add(new ObjectId("6077c104c61ee6347903b635"));
        list.add(new ObjectId("6077c104c61ee6347903b63a"));
        list.add(new ObjectId("6077c104c61ee6347903b63f"));
        list.add(new ObjectId("6077c104c61ee6347903b643"));
        list.add(new ObjectId("6077c104c61ee6347903b647"));
        list.add(new ObjectId("6077c104c61ee6347903b64f"));
        list.add(new ObjectId("6077c104c61ee6347903b655"));
        list.add(new ObjectId("6077c104c61ee6347903b65c"));

        Query query = Query.query(Criteria.where("questions.$id").in(list));
        List<Questionnaire> questionnaires = this.mongoTemplate.find(query, Questionnaire.class);
        System.out.println(questionnaires);
    }
}
package top.yusora.tanhua.dubbo.server.api.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.TestSoulApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.*;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author heyu
 */
@Slf4j
@Service
@DubboService(version = "1.0.0")
public class TestSoulApiImpl implements TestSoulApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final int OWL_BOUND = 21;

    private static final int RABBIT_BOUND = 41;

    private static final int FOX_BOUND = 56;


    /**
     * 获取问卷列表
     *
     * @return 问卷列表
     */
    @Override
    public List<Questionnaire> getQuestionnaires() {
        return this.mongoTemplate.findAll(Questionnaire.class);
    }

    /**
     * 根据用户id查找测试报告
     *
     * @param userId          用户id
     * @return 测试报告
     */
    @Override
    public TestReport getReport(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.findOne(query,TestReport.class);

    }

    /**
     * 获取问卷锁状态
     *
     * @param userId          用户id
     * @param questionnaireId 问卷Id
     * @return TestLock
     */
    @Override
    public TestLock getLockStatus(Long userId, ObjectId questionnaireId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("questionnaireId").is(questionnaireId));
        return this.mongoTemplate.findOne(query,TestLock.class);
    }

    /**
     * 根据回答，创建或更新测试报告
     * @param userId 用户Id
     * @param answerList 回答列表
     * @return 报告Id
     */
    @Override
    public String updateReport(Long userId, List<Answer> answerList) {
        //1.校验
        if(!ObjectUtil.isAllNotEmpty(userId,answerList)){
            return null;
        }
        List<String> questionIds = CollUtil.getFieldValues(answerList, "questionId", String.class);
        List<String> optionIds = CollUtil.getFieldValues(answerList, "optionId", String.class);

        //2.计算得分
        List<ObjectId> objectIds = optionIds.stream().map(ObjectId::new).collect(Collectors.toList());
        Query query = Query.query(Criteria.where("id").in(objectIds));
        List<Option> options = this.mongoTemplate.find(query, Option.class);
        if(CollUtil.isEmpty(options)){
            return null;
        }

        int sum = options.stream().mapToInt(Option::getPoint).sum();

        //3.查看报告是否存在
        TestReport report = this.getReport(userId);
        if(ObjectUtil.isNull(report)){
            //不存在则新建
            report = new TestReport();
            report.setId(ObjectId.get());
            report.setUserId(userId);
            report.setCreated(System.currentTimeMillis());
            report.setUpdated(report.getCreated());
        }
        else{
            report.setUpdated(System.currentTimeMillis());
        }
        //设置结果
        report.setResultId(this.getResultId(sum));

        //4.解锁下一级问题
        Boolean unlock = this.unlockNextLevel(userId, questionIds);
        if(!unlock){
            return null;
        }

        //5.保存用户报告
        this.mongoTemplate.save(report);
        return report.getId().toHexString();
    }

    /**
     * 根据报告Id获取测试结果
     *
     * @param reportId 报告编号
     * @return 测试结果
     */
    @Override
    public TestResult getResultByReportId(String reportId) {
        //获取报告
        TestReport report = this.mongoTemplate.findById(new ObjectId(reportId), TestReport.class);
        if(ObjectUtil.isNull(report)){
            return null;
        }
        //获取测试结果
        return this.mongoTemplate.findById(report.getResultId(), TestResult.class);
    }

    /**
     * 根据测试结果id获取有相同测试结果的用户（最多10个）
     *
     * @param resultId 测试结果id
     * @param userId 自己的用户Id（需排除）
     * @return 相似用户列表
     */
    @Override
    public List<Object> getSimilarUserId(ObjectId resultId, Long userId) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updated")));

        Query query = Query.query(Criteria.where("resultId").is(resultId).and("userId").nin(userId)).with(pageRequest);

        List<TestReport> reports = this.mongoTemplate.find(query, TestReport.class);

        if(CollUtil.isEmpty(reports)){
            return Collections.emptyList();
        }

        return CollUtil.getFieldValues(reports,"userId");
    }





    private Boolean unlockNextLevel(Long userId, List<String> questionIds){
        List<ObjectId> objectIds = questionIds.stream().map(ObjectId::new).collect(Collectors.toList());

        try {
            Query q1 = Query.query(Criteria.where("questions.$id").in(objectIds));
            Questionnaire questionnaire = this.mongoTemplate.findOne(q1, Questionnaire.class);
            if (ObjectUtil.isNull(questionnaire)) {
                return false;
            }

            //查询比当前星级高的问卷
            Integer star = questionnaire.getStar();
            Query q2 = Query.query(Criteria.where("star").gt(star)).with(Sort.by(Sort.Order.asc("star"))).limit(1);
            Questionnaire next = this.mongoTemplate.findOne(q2, Questionnaire.class);
            if (ObjectUtil.isNull(next)) {
                //没有更高星级 直接返回
                return true;
            }

            Query q3 = Query.query(Criteria.where("questionnaireId").is(next.getId())
                    .and("userId").is(userId));
            Update update = Update.update("isLock", false);
            UpdateResult upsert = this.mongoTemplate.upsert(q3, update, TestLock.class);

            return upsert.getMatchedCount() > 0;
        }catch (Exception e){
            log.error("解锁下一级问题失败～", e);
        }
        return false;
    }

    private ObjectId getResultId(int sum){
        if(sum < OWL_BOUND){
            //猫头鹰
            return new ObjectId("607773cdeaf3761bd7c957e8");
        }
        else if(sum < RABBIT_BOUND){
            //白兔
            return new ObjectId("607774ac9f4eff1abc53325f");
        }
        else if(sum < FOX_BOUND){
            //狐狸
            return new ObjectId("60777513f8c95f538f370011");
        }
        else {
            //狮子
            return new ObjectId("6077763b984d8f7d3beee947");
        }
    }


}

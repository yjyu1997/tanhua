package top.yusora.tanhua.dubbo.server.api;

import org.bson.types.ObjectId;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.*;

import java.util.List;

/**
 * @author heyu
 */
public interface TestSoulApi {
    /**
     * 获取问卷列表
     * @return 问卷列表
     */
    List<Questionnaire> getQuestionnaires();

    /**
     * 根据用户id获取报告
     * @param userId 用户id
     * @return 报告
     */
    TestReport getReport(Long userId);

    /**
     * 获取问卷锁状态
     * @param userId 用户id
     * @param questionnaireId 问卷Id
     * @return TestLock
     */
    TestLock getLockStatus(Long userId, ObjectId questionnaireId);

    /**
     * 根据回答，创建或更新测试报告
     * @param userId 用户Id
     * @param answerList 回答列表
     * @return 报告Id
     */
    String updateReport(Long userId, List<Answer> answerList);


    /**
     * 根据报告Id获取测试结果
     * @param reportId 报告编号
     * @return 测试结果
     */
    TestResult getResultByReportId(String reportId);


    /**
     * 根据测试结果id获取有相同测试结果的用户（最多10个）
     *
     * @param resultId 测试结果id
     * @param userId 自己的用户Id（需排除）
     * @return 相似用户列表
     */
    List<Object> getSimilarUserId(ObjectId resultId, Long userId);
}

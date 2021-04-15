package top.yusora.tanhua.server.service;

import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Answer;
import top.yusora.tanhua.server.vo.QuestionnaireVo;
import top.yusora.tanhua.server.vo.ReportVo;

import java.util.List;

/**
 * @author heyu
 */
public interface TestSoulService {

    /**
     * 拉取问卷列表
     * @return 问卷列表
     */
    List<QuestionnaireVo> getQuestionnaires();

    /**
     * 根据回答，创建或更新测试报告
     * @param answerList 回答列表
     * @return 报告Id
     */
    String updateReport(List<Answer> answerList);

    /**
     * 根据报告Id获取测试测试结果
     * @param reportId 报告Id
     * @return 测试结果
     */
    ReportVo getReport(String reportId);
}

package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.TestSoulApi;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.*;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.server.service.TestSoulService;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Answer;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.vo.*;
import top.yusora.tanhua.utils.UserThreadLocal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author heyu
 */
@Service
@Slf4j
public class TestSoulServiceImpl implements TestSoulService {

    @DubboReference(version = "1.0.0")
    private TestSoulApi testSoulApi;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * @return 问卷列表
     */
    @Override
    public List<QuestionnaireVo> getQuestionnaires() {
        //1.获取用户上下文
        Long userId = UserThreadLocal.get();

        //2.RPC调用获取问卷列表
        List<Questionnaire> questionnaires = this.testSoulApi.getQuestionnaires();

        if(CollUtil.isEmpty(questionnaires)){
            //判空
            return null;
        }

        List<QuestionnaireVo> questionnaireVos = new ArrayList<>();

        //3.获取report
        TestReport report = this.testSoulApi.getReport(userId);

        for (Questionnaire questionnaire : questionnaires) {
            QuestionnaireVo questionnaireVo = BeanUtil.toBeanIgnoreError(questionnaire, QuestionnaireVo.class);
            //设置id
            questionnaireVo.setId(questionnaire.getId().toHexString());
            //设置reportId
            if(ObjectUtil.isNotEmpty(report)){
                questionnaireVo.setReportId(report.getId().toHexString());
            }

            //设置questions
            questionnaireVo.setQuestions(questionnaire.getQuestions().parallelStream()
            .map(this::fillQuestionsVo).collect(Collectors.toList()));

            //设置isLock
            Boolean isLock = Optional.ofNullable(this.testSoulApi.getLockStatus(userId, questionnaire.getId()))
                             .map(TestLock::getIsLock).orElse(true);

            questionnaireVo.setIsLock(isLock ? 1 : 0);

            questionnaireVos.add(questionnaireVo);
        }

        return questionnaireVos;
    }

    /**
     * 根据回答，创建或更新测试报告
     *
     * @param answerList 回答列表
     * @return 报告Id
     */
    @Override
    public String updateReport(List<Answer> answerList) {

        Long userId = UserThreadLocal.get();
        return this.testSoulApi.updateReport(userId,answerList);
    }

    /**
     * 根据报告Id获取测试测试结果
     *
     * @param reportId 报告Id
     * @return 测试结果
     */
    @Override
    public ReportVo getReport(String reportId) {

        Long userId = UserThreadLocal.get();

        //远程调用获取测试结果
        TestResult testResult = this.testSoulApi.getResultByReportId(reportId);
        if(ObjectUtil.isNull(testResult)){
            return null;
        }

        //init
        ReportVo reportVo = new ReportVo();
        reportVo.setConclusion(testResult.getConclusion());
        reportVo.setDimensions(testResult.getDimensions());
        reportVo.setCover(testResult.getCover());

        //获取相似的用户id
        List<Object> userIds = this.testSoulApi.getSimilarUserId(testResult.getId(),userId);

        if(CollUtil.isEmpty(userIds)){
            return reportVo;
        }


        List<UserInfo> userInfos = this.userInfoService.queryUserInfoList(userIds);

        List<SimilarYou> similarYous = userInfos.parallelStream().map(userInfo -> {
            SimilarYou similarYou = new SimilarYou();
            similarYou.setId(Convert.toInt(userInfo.getUserId()));
            similarYou.setAvatar(userInfo.getLogo());
            return similarYou;
        }).collect(Collectors.toList());

        reportVo.setSimilarYou(similarYous);

        return reportVo;
    }

    private QuestionsVo fillQuestionsVo(Questions questions){
        QuestionsVo questionsVo = new QuestionsVo();
        questionsVo.setQuestion(questions.getQuestion());
        questionsVo.setId(questions.getId().toHexString());
        //并行流 提升效率 Collectors保证线程安全
        //Fork/Join的思想是分治，先拆分任务，再合并结果，每个任务都用单独的线程去处理。所以虽然它同样使用ArrayList，
        // 但是我们看到他会为每个线程都创建一个ArrayList对象，最后用addAll方法把它们合并起来，
        // 每个线程操作的是自己的集合对象，自然不会有线程安全问题。
        questionsVo.setOptions(questions.getOptions().parallelStream()
                .map(this::fillOptionVo).collect(Collectors.toList()));

        return questionsVo;
    }

    private OptionVo fillOptionVo(Option option){
            OptionVo optionVo = new OptionVo();
            optionVo.setId(option.getId().toHexString());
            optionVo.setOption(option.getOption());
            return optionVo;
    }
}

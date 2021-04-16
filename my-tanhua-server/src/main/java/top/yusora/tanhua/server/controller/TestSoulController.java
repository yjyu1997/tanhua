package top.yusora.tanhua.server.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.TestSoulService;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Answer;
import top.yusora.tanhua.server.vo.QuestionnaireVo;
import top.yusora.tanhua.server.vo.ReportVo;
import top.yusora.tanhua.utils.Cache;

import java.util.List;
import java.util.Optional;

/**
 * @author heyu
 */
@RestController
@Slf4j
@RequestMapping("/testSoul")
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;

    @GetMapping
    @Cache(time = "10")
    public List<QuestionnaireVo> getQuestionnaires(){
        try{
            return Optional.ofNullable(this.testSoulService.getQuestionnaires())
                    .orElseThrow(() -> CastException.cast(ErrorCode.GET_QUESTIONNAIRES_FAILED));
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.GET_QUESTIONNAIRES_FAILED);
    }


    @PostMapping
    public String getReportId(@RequestBody String answers){
        try {
            JSONObject jsonObject = JSONUtil.parseObj(answers);
            JSONArray jsonArray = jsonObject.getJSONArray("answers");
            List<Answer> answerList = jsonArray.toList(Answer.class);
            String reportId = this.testSoulService.updateReport(answerList);
            if(StrUtil.isNotEmpty(reportId)){
                return reportId;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.SUBMIT_QUESTIONNAIRES_FAILED);
    }



    @GetMapping("/report/{id}")
    @Cache(time = "10")
    public ReportVo getReport(@PathVariable("id") String reportId){
        try{
            return Optional.ofNullable(this.testSoulService.getReport(reportId))
                    .orElseThrow(() -> CastException.cast(ErrorCode.GET_REPORT_FAILED));
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.GET_REPORT_FAILED);
    }

}

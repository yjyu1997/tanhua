package top.yusora.tanhua.dubbo.server.api.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.QuestionApi;
import top.yusora.tanhua.dubbo.server.mapper.QuestionMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Question;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
@Slf4j
public class QuestionApiImpl implements QuestionApi {

    @Autowired
    private QuestionMapper questionMapper;
    /**
     * 根据用户id检索相应问题
     *
     * @param userId 用户id
     * @return 问题
     */
    @Override
    public Question queryQuestion(Long userId) {

        return new LambdaQueryChainWrapper<>(this.questionMapper)
                .eq(Question::getUserId,userId).one();
    }

    /**
     * 保存该用户的陌生人问题
     *
     * @param userId  用户id
     * @param content 问题内容
     */
    @Override
    public void save(Long userId, String content) {
        Question question = this.queryQuestion(userId);
        if (null != question) {
            question.setTxt(content);
            this.questionMapper.updateById(question);
        } else {
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            this.questionMapper.insert(question);
        }
    }
}

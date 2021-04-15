package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heyu
 */

public class Answer implements Serializable{

    private static final long serialVersionUID = 1178024959174663230L;
    private String questionId;

    private String optionId;

    public Answer() {
    }

    public Answer(String questionId, String optionId) {
        this.questionId = questionId;
        this.optionId = optionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "questionId='" + questionId + '\'' +
                ", optionId='" + optionId + '\'' +
                '}';
    }
}

package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class QuestionnaireVo implements Serializable {
    private static final long serialVersionUID = 8072244274068193174L;
    /**
     * @Description 主键id
     */

    private String id;

    /**
     * @Description 问卷名称
     */
    private String name;


    private String level;

    private String cover;

    private Integer star;


    private List<QuestionsVo> questions;

    private Integer isLock;

    private String reportId;


}

package top.yusora.tanhua.server.vo;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 今日佳人
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayBest implements Serializable {
    private static final long serialVersionUID = -5291021490051001551L;
    /**
     * @Description 被推荐用户ID
     */
    @Alias("userId")
    private Long id;
    /**
     * @Description 被推荐用户头像
     */
    @Alias("logo")
    private String avatar;
    /**
     * @Description 被推荐用户昵称
     */
    @Alias("nickName")
    private String nickname;
    /**
     * @Description  性别 man woman
     */
    private String gender;
    /**
     * @Description 被推荐用户年龄
     */
    private Integer age;
    /**
     * @Description 被推荐用户标签
     */
    private String[] tags;
    /**
     * @Description 缘分值
     */
    private Long fateValue;

}

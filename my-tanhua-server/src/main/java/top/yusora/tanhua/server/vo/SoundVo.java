package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoundVo implements Serializable {
    private static final long serialVersionUID = 2482313667092214536L;
    /**
     * @Description 用户ID
     */
    private Long id;

    /**
     * @Description 头像
     */
    private String avatar;
    /**
     * @Description 昵称
     */
    private String nickname;

    /**
     * @Description //年龄
     */
    private Integer age;

    /**
     * @Description //性别 man woman
     */
    private String gender;

    /**
     * @Description 语音URL
     */
    private String soundUrl;

    /**
     * @Description 剩余次数
     */
    private Integer remainingTimes;
}

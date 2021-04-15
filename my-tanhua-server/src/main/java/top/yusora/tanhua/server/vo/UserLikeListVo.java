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
public class UserLikeListVo implements Serializable {

    private static final long serialVersionUID = 563978458022718859L;
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;
    private String education;
    /**
     * @Description 婚姻状态（0未婚，1已婚）
     */
    private Integer marriage;

    /**
     * @Description //匹配度
     */
    private Integer matchRate;
    /**
     * @Description //是否喜欢ta
     */
    private Boolean alreadyLove;

}
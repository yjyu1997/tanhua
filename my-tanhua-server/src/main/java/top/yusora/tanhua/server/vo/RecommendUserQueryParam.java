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
public class RecommendUserQueryParam implements Serializable {

    private static final long serialVersionUID = -5396838012675948472L;
    /**
     * @Description 当前页数
     */
    private Integer page = 1;
    /**
     * @Description 页尺寸
     */
    private Integer pagesize = 10;
    /**
     * @Description 性别 man woman
     */
    private String gender;
    /**
     * @Description 近期登陆时间
     */
    private String lastLogin;
    /**
     * @Description 年龄
     */
    private Integer age;
    /**
     *  @Description 居住地
     */
    private String city;
    /**
     * @Description 学历
     */
    private String education;
}

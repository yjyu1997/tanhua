package top.yusora.tanhua.server.vo;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 仅用于显示首页页面上的访客
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorsVo implements Serializable {

    private static final long serialVersionUID = 6406880060717189211L;
    @Alias("userId")
    private Long id;
    @Alias("logo")
    private String avatar;
    @Alias("nickName")
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private Integer fateValue;

}
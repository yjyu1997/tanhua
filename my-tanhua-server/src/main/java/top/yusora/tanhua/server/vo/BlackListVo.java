package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListVo implements Serializable {

    private static final long serialVersionUID = 6092295955716069815L;
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;

}


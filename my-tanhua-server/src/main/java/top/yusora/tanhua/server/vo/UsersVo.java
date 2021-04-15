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
public class UsersVo implements Serializable {


    private static final long serialVersionUID = 8167650887074275689L;
    private Long id;
    private String userId;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;

}
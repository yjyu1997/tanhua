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
public class HuanXinUserVo implements Serializable {

    private static final long serialVersionUID = 3571078839348379215L;
    /**
     * @Description 用户名
     */
    private String username;
    /**
     * @Description 密码
     */
    private String password;

}


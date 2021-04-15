package top.yusora.tanhua.server.vo;

import cn.hutool.core.annotation.Alias;
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
public class NearUserVo implements Serializable {

    private static final long serialVersionUID = 4259633100145079895L;
    private Long userId;
    @Alias("logo")
    private String avatar;
    @Alias("nickName")
    private String nickname;

}


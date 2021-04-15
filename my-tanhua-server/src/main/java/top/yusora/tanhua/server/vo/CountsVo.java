package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountsVo implements Serializable {

    private static final long serialVersionUID = 6312934072054638526L;
    /**
     * @Description //互相喜欢
     */
    private Long eachLoveCount;
    /**
     * @Description //喜欢
     */
    private Long loveCount;
    /**
     * @Description //粉丝
     */
    private Long fanCount;

}
package top.yusora.tanhua.dubbo.server.pojo.po.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BasePojo;

/**
 * @author heyu
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BasePojo {

    private static final long serialVersionUID = -318262758711530228L;
    private Long id;
    private Long userId;
    /**
     * @Description  问题内容
     */
    private String txt;

}
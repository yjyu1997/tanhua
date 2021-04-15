package top.yusora.tanhua.dubbo.server.pojo.po.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BlackList extends BasePojo {

    private static final long serialVersionUID = 7146747726876991055L;
    private Long id;
    private Long userId;
    private Long blackUserId;
}


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
public class Settings extends BasePojo {

    private static final long serialVersionUID = -1629100212998913848L;
    private Long id;
    private Long userId;
    private Boolean likeNotification = true;
    private Boolean pinglunNotification = true;
    private Boolean gonggaoNotification = true;

}
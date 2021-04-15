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
public class SettingsVo implements Serializable {

    private static final long serialVersionUID = 8478389468617257189L;
    private Long id;
    /**
     * @Description 陌生人问题
     */
    private String strangerQuestion = "";
    /**
     * @Description 手机号
     */
    private String phone;
    /**
     * @Description 推送喜欢通知
     */
    private Boolean likeNotification = true;
    /**
     * @Description 推送评论通知
     */
    private Boolean pinglunNotification = true;
    /**
     * @Description 推送公告通知
     */
    private Boolean gonggaoNotification = true;

}
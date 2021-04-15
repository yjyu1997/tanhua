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
public class AnnouncementVo implements Serializable {

    private static final long serialVersionUID = -1297722809687000630L;
    /**
     * @Description 公告Id
     */
    private String id;
    /**
     * @Description 公告标题
     */
    private String title;
    /**
     * @Description 公告描述
     */
    private String description;
    /**
     * @Description 创建时间
     */
    private String createDate;

}
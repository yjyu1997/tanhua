package top.yusora.tanhua.dubbo.server.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BasePojo;

/**
 * @author heyu
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BasePojo {

    private static final long serialVersionUID = 5756935838850975732L;
    /**
     * @Description 公告id
     */
    @TableId
    private Long id;
    /**
     * @Description 公告标题
     */
    private String title;
    /**
     * @Description 公告描述
     */
    private String description;

}


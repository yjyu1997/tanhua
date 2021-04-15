package top.yusora.tanhua.dubbo.server.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author heyu
 */
@Data
public abstract class BasePojo implements Serializable {

    /**
     * 插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime created;

    /**
     * 插入&&修改时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updated;
}

package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author heyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {
    private static final long serialVersionUID = -2105385689859184204L;

    /**
     * @Description 总条数
     */
    private Integer counts = 0;

    /**
     * @Description 当前页
     */
    private Integer page = 0;

    /**
     * @Description 一页显示的大小
     */
    private Integer pagesize = 0;

    /**
     * @Description 总页数
     */
    private Integer pages = 0;

    /**
     * 数据列表
     */
    private List<?> items = Collections.emptyList();
}

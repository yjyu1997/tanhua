package top.yusora.tanhua.dubbo.server.pojo.dto;

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
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = -2105385689859184204L;

    /**
     * 总条数
     */
    private Integer total = 0;

    /**
     * 当前页
     */
    private Integer pageNum = 0;

    /**
     * 一页显示的大小
     */
    private Integer pageSize = 0;

    /**
     * 数据列表
     */
    private List<T> records = Collections.emptyList();
}

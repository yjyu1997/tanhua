package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Dimension;

import java.io.Serializable;
import java.util.List;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportVo implements Serializable {
    private static final long serialVersionUID = -2562620456684248695L;
    private String conclusion;

    private String cover;

    private List<Dimension> dimensions;

    private List<SimilarYou> similarYou;

}

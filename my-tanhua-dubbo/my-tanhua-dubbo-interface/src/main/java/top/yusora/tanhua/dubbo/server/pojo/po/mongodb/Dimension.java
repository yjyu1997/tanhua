package top.yusora.tanhua.dubbo.server.pojo.po.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dimension implements Serializable {
    private static final long serialVersionUID = -4384854848520617454L;
    private String key;

    private String value;
}

package top.yusora.tanhua.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@Builder
public class ErrorResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String errCode;
    private String errMessage;
}

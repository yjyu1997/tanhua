package top.yusora.tanhua.vo;

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
public class PicUploadResult implements Serializable {
    private static final long serialVersionUID = 5836369123047501122L;
    /**
     * @Description  文件唯一标识
     */
    private String uid;

    /**
     * @Description 文件名
     */
    private String name;

    /**
     * @Description 状态有：uploading done error removed
      */
    private String status;

    /**
     * @Description 服务端响应内容，如：'{"status": "success"}'
      */
    private String response;
}

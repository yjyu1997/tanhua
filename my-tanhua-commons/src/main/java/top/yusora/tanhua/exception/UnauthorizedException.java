package top.yusora.tanhua.exception;

import cn.hutool.http.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author heyu
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UnauthorizedException extends RuntimeException {

    private String url;
    private String body;
    private Method method;

}
package top.yusora.tanhua.exception;



import lombok.extern.slf4j.Slf4j;
import top.yusora.tanhua.constant.ErrorCode;

/**
 * 异常抛出类
 */
@Slf4j
public class CastException {
    public static ProjectException cast(ErrorCode errorCode) {
        log.error(errorCode.getMsg());
        return new ProjectException(errorCode);
    }

    public static void throwCast(ErrorCode errorCode){
        log.error(errorCode.getMsg());
        throw new ProjectException(errorCode);
    }
}

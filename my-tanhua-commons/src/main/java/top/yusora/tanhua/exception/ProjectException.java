package top.yusora.tanhua.exception;


import top.yusora.tanhua.constant.ErrorCode;

/**
 * 自定义异常
 * @author heyu
 */
public class ProjectException extends RuntimeException{

    private static final long serialVersionUID = 2612710783540313857L;
    /**
     * @Description 错误码
     */
    private ErrorCode errorCode;


    public ProjectException(ErrorCode errorCode) {

        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ProjectException{" +
                "code='" + errorCode.getCode() + '\'' +
                ", message='" + errorCode.getMsg() + '\'' +
                '}';
    }


}

package top.yusora.tanhua.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.ProjectException;
import top.yusora.tanhua.vo.ErrorResult;

/**
 * 异常的统一处理，如果Controller中没有处理异常会在这里捕获处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleExcepting(Exception e) {
        ProjectException projectException = null;
        log.error("error", e);
        if(e instanceof ProjectException){
            projectException = (ProjectException) e;
        }else{
            projectException= new ProjectException(ErrorCode.UNKNOWN_ERROR);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResult.builder()
                        .errMessage(projectException.getErrorCode().getMsg())
                        .errCode(projectException.getErrorCode().getCode())
                        .build());
    }

}


package top.yusora.tanhua.sso.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yusora.tanhua.constant.SSOCode;
import top.yusora.tanhua.vo.ErrorResult;
import top.yusora.tanhua.sso.service.impl.SmsServiceImpl;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class SmsController {
    @Autowired
    private SmsServiceImpl smsService;

    /**
     * @Description 发送验证码
     */
    @PostMapping("/login")
    public Object sendCheckCode(@RequestBody Map<String,Object> param){
        String phone = String.valueOf(param.get("phone"));
        ErrorResult errorResult = null;
        try{
             errorResult = smsService.sendCheckCode(phone);
            if (null == errorResult) {
                return null;
            }
        }catch (Exception e){
            log.error("发送短信验证码失败~ phone = " + phone, e);
            errorResult = ErrorResult.builder().errCode(SSOCode.SSO_SEND_SMS_FAILED.getErrCode())
                    .errMessage(SSOCode.SSO_SEND_SMS_FAILED.getErrMessage())
                    .build();
        }
        return errorResult;
    }
}

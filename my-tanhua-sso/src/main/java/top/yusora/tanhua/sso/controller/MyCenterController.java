package top.yusora.tanhua.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.sso.service.MyCenterService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("users")
public class MyCenterController {

    @Autowired
    private UserInfoController userInfoController;

    @Autowired
    private MyCenterService myCenterService;

    /**
     * 上传头像
     *
     * @param file 头像
     * @param token jwt token
     * @return null
     */
    @PostMapping("header")
    public Object saveLogo(@RequestParam("headPhoto") MultipartFile file, @RequestHeader("Authorization") String token) {
        return this.userInfoController.saveUserLogo(file, token);
    }

    /**
     * 发送短信验证码
     *
     * @return null
     */
    @PostMapping("phone/sendVerificationCode")
    public Void sendVerificationCode(@RequestHeader("Authorization") String token) {
        try {
            boolean bool = this.myCenterService.sendVerificationCode(token);
            if (bool) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.SSO_SEND_SMS_FAILED);
    }


    /**
     * 校验验证码
     * @param param 用户输入的验证码
     * @param token jwt token
     * @return 校验是否成功
     */
    @PostMapping("phone/checkVerificationCode")
    public Map<String, Object> checkVerificationCode(@RequestBody Map<String, String> param,
                                                     @RequestHeader("Authorization") String token) {
        try {
            String code = param.get("verificationCode");
            Boolean bool = this.myCenterService.checkVerificationCode(code, token);
            Map<String, Object> result = new HashMap<>();
            result.put("verification", bool);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.SSO_VALIDATE_CHECKCODE_FAILED);
    }


    /**
     * 保存新手机号
     * @param param 用户新手机号
     * @param token jwt token
     * @return null
     */
    @PostMapping("phone")
    public Void updatePhone(@RequestBody Map<String, String> param,
                                            @RequestHeader("Authorization") String token) {
        try {
            String newPhone = param.get("phone");
            boolean bool = this.myCenterService.updatePhone(token, newPhone);
            if (bool) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.SSO_UPDATE_PHONE_FAILED);
    }
}
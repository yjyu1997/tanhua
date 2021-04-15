package top.yusora.tanhua.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yusora.tanhua.constant.SSOCode;
import top.yusora.tanhua.vo.ErrorResult;
import top.yusora.tanhua.sso.service.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author heyu
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/loginVerification")
    public Object login(@RequestBody Map<String, String> param){
        try{
            String phone = param.get("phone");
            String code = param.get("verificationCode");

            //返回值： isNew ｜ token
            String data = this.userService.login(phone, code);

            if(StringUtils.isNotEmpty(data)){
                //登陆成功
                Map<String, Object> result = new HashMap<>(2);
                String[] ss = StringUtils.split(data,'|');
                result.put("isNew",Boolean.valueOf(ss[0]));
                result.put("token",ss[1]);
                //将 isNew 和 Token 返回给客户端保存
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ErrorResult.builder()
                .errCode(SSOCode.SSO_USER_LOGIN_FAILED.getErrCode())
                .errMessage(SSOCode.SSO_USER_LOGIN_FAILED.getErrMessage())
                .build();
    }


}

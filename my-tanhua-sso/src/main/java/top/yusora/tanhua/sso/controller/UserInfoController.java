package top.yusora.tanhua.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.constant.SSOCode;
import top.yusora.tanhua.vo.ErrorResult;
import top.yusora.tanhua.sso.service.impl.UserInfoServiceImpl;

import java.util.Map;

/**
 * @author heyu
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {
    @Autowired
    private UserInfoServiceImpl userInfoService;

    /**
     * @Description  完善个人信息-基本信息
     * @param param 个人信息列表
     * @param token 登陆jwt Token
     * @return 成功或错误信息
     */
    @PostMapping("/loginReginfo")
    public Object saveUserInfo(@RequestBody Map<String, String> param,
                                               @RequestHeader("Authorization") String token){
        try{
            Boolean saveInfoFlag = this.userInfoService.saveUserInfo(param, token);
            if(saveInfoFlag){
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ErrorResult.builder()
                .errCode(SSOCode.SSO_SAVE_USER_INFO_FAILED.getErrCode())
                .errMessage(SSOCode.SSO_SAVE_USER_INFO_FAILED.getErrMessage())
                .build();

    }

    /**
     * @Description 完善个人信息-头像
     * @param file 头像
     * @param token JwtToken
     * @return 业务完成状态
     */
    @PostMapping("loginReginfo/head")
    public Object saveUserLogo(@RequestParam("headPhoto") MultipartFile file,
                                               @RequestHeader("Authorization") String token){
        try {
            Boolean saveLogoFlag = this.userInfoService.saveUserLogo(file, token);
            if (saveLogoFlag) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ErrorResult.builder()
                .errCode(SSOCode.SSO_SAVE_USER_LOGO_FAILED.getErrCode())
                .errMessage(SSOCode.SSO_SAVE_USER_LOGO_FAILED.getErrMessage())
                .build();

    }


}

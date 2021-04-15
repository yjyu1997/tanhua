package top.yusora.tanhua.sso.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author heyu
 */
public interface UserInfoService {


    /**
     * 保存用户基本信息
     * @param param 基本信息
     * @param token JWT token
     * @return 是否成功
     */
    Boolean saveUserInfo(Map<String, String> param, String token);



    /**
     * 保存用户头像
     * @param file 头像图片
     * @param token JWT token
     * @return 是否成功
     *
     */
    Boolean saveUserLogo(MultipartFile file, String token);
}

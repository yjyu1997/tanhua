package top.yusora.tanhua.sso.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.dubbo.server.api.UserInfoApi;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.sso.service.FaceImageService;
import top.yusora.tanhua.service.PicUploadService;
import top.yusora.tanhua.sso.service.UserInfoService;
import top.yusora.tanhua.sso.service.UserService;
import top.yusora.tanhua.vo.PicUploadResult;
import top.yusora.tanhua.utils.EmptyUtil;
import top.yusora.tanhua.utils.ExceptionsUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author heyu
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserService userService;

    @DubboReference(version = "1.0.0")
    private UserInfoApi userInfoApi;

    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private PicUploadService picUploadService;

    /**
     * 保存用户基本信息
     * @param param 基本信息
     * @param token JWT token
     * @return 是否成功
     */
    @Override
    public Boolean saveUserInfo(Map<String, String> param, String token) {

        // 检验token,从redis中取出user对象
        Long userId = this.userService.checkToken(token);
        if (EmptyUtil.isNullOrEmpty(userId)) {
            return false;
        }

        //获取参数
        String gender = param.get("gender");
        String nickname = param.get("nickname");
        String birthday = param.get("birthday");
        String city = param.get("city");

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setSex(StrUtil.equalsIgnoreCase(gender, "male") ? SexEnum.MAN : SexEnum.WOMAN);
        userInfo.setAge(this.calAge(birthday));
        userInfo.setNickName(nickname);
        userInfo.setBirthday(birthday);
        userInfo.setCity(city);

        try {
            return this.userInfoApi.save(userInfo);
        } catch (Exception e) {
            log.error("保存用户信息异常，原因：{}", ExceptionsUtil.getStackTraceAsString(e));
            return false;
        }
    }

    /**
     * 保存用户头像
     * @param file 头像图片
     * @param token JWT token
     * @return 是否成功
     *
     */
    @Override
    public Boolean saveUserLogo(MultipartFile file, String token) {

        //校验token
        Long userId  = this.userService.checkToken(token);
        if (EmptyUtil.isNullOrEmpty(userId)) {
            return false;
        }
        //图片上传到阿里云OSS
        PicUploadResult result = this.picUploadService.upload(file);
        if (EmptyUtil.isNullOrEmpty(result.getName())) {
            //上传失败
            return false;
        }
        //校验图片是否是人像，如果不是人像就返回false
        boolean isPortrait = this.faceImageService.checkIsPortrait(result.getName());
        if (!isPortrait) {
            return false;
        }
        try {
            //保存头像url到数据库表中
            UserInfo userInfo = new UserInfo();
            userInfo.setLogo(result.getName());
            userInfo.setUserId(userId);
            return this.userInfoApi.update(userInfo);

        } catch (Exception e) {
            log.error("保存用户头像异常，原因：{}", ExceptionsUtil.getStackTraceAsString(e));
            return false;
        }
    }


    private Integer calAge(String birthday){
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(birthday, pattern);
        int year = date.getYear();
        int yearOfNow = LocalDate.now().getYear();
        return yearOfNow-year;
    }
}

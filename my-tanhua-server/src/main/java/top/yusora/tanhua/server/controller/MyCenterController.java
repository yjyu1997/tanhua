package top.yusora.tanhua.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.MyCenterService;
import top.yusora.tanhua.server.vo.CountsVo;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.SettingsVo;
import top.yusora.tanhua.server.vo.UserInfoVo;
import top.yusora.tanhua.utils.Cache;

import java.util.Map;
import java.util.Optional;

@RequestMapping("users")
@RestController
@Slf4j
public class MyCenterController {

    @Autowired
    private MyCenterService myCenterService;

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id，如果为空，表示查询当前登录人的信息
     * @return 用户信息
     */
    @Cache
    @GetMapping
    public UserInfoVo queryUserInfoByUserId(@RequestParam(value = "userID", required = false) Long userId) {

        return Optional.ofNullable(this.myCenterService.queryUserInfoByUserId(userId))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_USER_INFO_FAILED));
    }

    /**
     * 是否喜欢
     *
     * @param userId 对方用户id
     * @return 是否喜欢
     */
    @GetMapping("{userId}/alreadyLove")
    public Boolean isLike(@PathVariable("userId") Long userId){
        return this.myCenterService.isLike(userId);
    }


    /**
     * 更新用户信息
     *
     * @param userInfoVo 需更新的用户基本信息
     * @return null
     */
    @PutMapping
    public Void updateUserInfo(@RequestBody UserInfoVo userInfoVo){
        try {
            Boolean result = this.myCenterService.updateUserInfo(userInfoVo);
            if(result){
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.UPDATE_USER_INFO_FAILED);
    }


    /**
     * 互相喜欢，喜欢，粉丝 - 统计
     *
     * @return 互相喜欢，喜欢，粉丝数量
     */
    @GetMapping("counts")
    public CountsVo queryCounts(){
        try {
            return this.myCenterService.queryCounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.QUERY_LIKE_USER_COUNT_FAILED);
    }


    /**
     * 互相关注、我关注、粉丝、谁看过我 - 翻页列表
     *
     * @param type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @param nickname 昵称
     * @return 相应用户列表
     */
    @Cache(time = "10")
    @GetMapping("friends/{type}")
    public PageResult queryLikeList(@PathVariable("type") String type,
                                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                    @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                                    @RequestParam(value = "nickname", required = false) String nickname) {
        try {
            page = Math.max(1, page);
            return this.myCenterService.queryLikeList(Integer.valueOf(type), page, pageSize, nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.QUERY_USER_LIKE_LIST_FAILED);
    }

    /**
     * 取消喜欢
     *
     * @param userId 对方用户id
     * @return null
     */
    @DeleteMapping("like/{uid}")
    public Void disLike(@PathVariable("uid") Long userId) {
        try {
            this.myCenterService.disLike(userId);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.DISLIKE_USER_FAILED);
    }



    /**
     * 关注粉丝
     *
     * @param userId 对方用户id
     * @return null
     */
    @PostMapping("fans/{uid}")
    public Void likeFan(@PathVariable("uid") Long userId){
        try {
            this.myCenterService.likeFan(userId);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.LIKE_USER_FAILED);
    }

    /**
     * 查询配置
     *
     * @return 用户配置信息
     */
    @GetMapping("settings")
    public SettingsVo querySettings() {
        try {
            return this.myCenterService.querySettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.QUERY_SETTINGS_FAILED);
    }


    /**
     * 设置陌生人问题
     * @param param 请求体参数
     * @return null
     */
    @PostMapping("questions")
    public Void saveQuestions(@RequestBody Map<String, String> param) {
        try {
            String content = param.get("content");
            this.myCenterService.saveQuestions(content);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.SETTING_QUESTION_FAILED);
    }


    /**
     * 查询黑名单
     *
     * @param page 当前页数
     * @param pagesize 每页条数
     * @return 黑名单分页列表
     */
    @GetMapping("blacklist")
    public PageResult queryBlacklist(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                     @RequestParam(value = "pagesize", defaultValue = "10") Integer pagesize) {
        try {
            return this.myCenterService.queryBlacklist(page, pagesize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.QUERY_BLACKLIST_FAILED);
    }


    /**
     * 移除黑名单
     *
     * @param userId 用户id
     * @return null
     */
    @DeleteMapping("blacklist/{uid}")
    public Void delBlacklist(@PathVariable("uid") Long userId) {
        try {
            this.myCenterService.delBlacklist(userId);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.DEL_BLACKLIST_FAILED);
    }


    /**
     * 更新通知设置
     *
     * @param param 请求体参数
     * @return null
     */
    @PostMapping("notifications/setting")
    public Void updateNotification(@RequestBody Map<String, Boolean> param) {
        try {
            Boolean likeNotification = param.get("likeNotification");
            Boolean pinglunNotification = param.get("pinglunNotification");
            Boolean gonggaoNotification = param.get("gonggaoNotification");

            this.myCenterService.updateNotification(likeNotification, pinglunNotification, gonggaoNotification);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.UPDATE_NOTIFICATION_FAILED);
    }
}


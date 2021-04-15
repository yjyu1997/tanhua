package top.yusora.tanhua.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.TanhuaService;
import top.yusora.tanhua.server.vo.NearUserVo;
import top.yusora.tanhua.server.vo.TodayBest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author heyu
 */
@RequestMapping("tanhua")
@RestController
public class TanHuaController {

    @Autowired
    private TanhuaService tanHuaService;

    /**
     * 查询个人主页的个人信息
     *
     * @param userId 用户id
     * @return 个人信息（可复用今日佳人）
     */
    @GetMapping("{id}/personalInfo")
    public TodayBest queryUserInfo(@PathVariable("id") Long userId) {

       return Optional.ofNullable(this.tanHuaService.queryUserInfo(userId))
               .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_USER_INFO_FAILED));
    }

    /**
     * 查询陌生人问题
     *
     * @param userId 陌生人Id
     * @return 问题
     */
    @GetMapping("strangerQuestions")
    public String queryQuestion(@RequestParam("userId") Long userId) {

        return this.tanHuaService.queryQuestion(userId);
    }

    /**
     * 回复陌生人问题
     * @param param post请求体
     *
     */
    @PostMapping("strangerQuestions")
    public Void replyQuestion(@RequestBody Map<String, Object> param) {

            Long userId = Long.valueOf(param.get("userId").toString());
            String reply = param.get("reply").toString();
            Boolean result = this.tanHuaService.replyQuestion(userId, reply);
            if (result) {
                return null;
            }
            throw CastException.cast(ErrorCode.REPLY_QUESTION_FAILED);
    }

    /**
     * 搜附近
     *
     * @param gender 性别
     * @param distance 距离
     * @return 附近的人列表
     */
    @GetMapping("search")
    public List<NearUserVo> queryNearUser(@RequestParam(value = "gender", required = false) String gender,
                                                          @RequestParam(value = "distance", defaultValue = "2000") String distance) {

        return this.tanHuaService.queryNearUser(gender, distance);
    }


    /**
     * 探花
     *
     * @return 探花卡片列表
     */
    @GetMapping("cards")
    public List<TodayBest> queryCardsList() {

        return this.tanHuaService.queryCardsList();
    }

    /**
     * 喜欢:右滑
     *
     * @param likeUserId 对方用户Id
     * @return null
     */
    @GetMapping("{id}/love")
    public Void likeUser(@PathVariable("id") Long likeUserId) {
        try{
            if(this.tanHuaService.likeUser(likeUserId)){
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.LIKE_USER_FAILED);
    }

    /**
     * 不喜欢：左滑
     *
     * @param likeUserId 对方用户id
     * @return null
     */
    @GetMapping("{id}/unlove")
    public Void notLikeUser(@PathVariable("id") Long likeUserId) {
        try{
            if(this.tanHuaService.notLikeUser(likeUserId)){
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        throw CastException.cast(ErrorCode.DISLIKE_USER_FAILED);
    }

}


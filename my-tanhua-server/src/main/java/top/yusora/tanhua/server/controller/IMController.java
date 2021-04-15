package top.yusora.tanhua.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.IMService;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.UserInfoVo;
import top.yusora.tanhua.utils.NoAuthorization;

import java.util.Map;
import java.util.Optional;

/**
 * @author heyu
 */
@RequestMapping("/messages")
@RestController
@Slf4j
public class IMController {

    @Autowired
    private IMService imService;

    /**
     * 根据环信用户名查询用户信息
     *
     * @param userName 环信用户
     * @return 用户基本信息
     */
    @GetMapping("/userinfo")
    @NoAuthorization
    public UserInfoVo queryUserInfoByUserName(@RequestParam("huanxinId") String userName) {
        log.info("通过环信用户名{}查询信息",userName);
        return Optional.ofNullable(this.imService.queryUserInfoByUserName(userName))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_HUANXIN_USER_INFO_FAILED));

    }

    /**
     * 添加好友
     *
     * @param param 参数：userId 好友Id
     * @return null
     */
    @PostMapping("/contacts")
    public Void contactUser(@RequestBody Map<String, Object> param) {

        Long friendId = Long.valueOf(param.get("userId").toString());
        boolean result = this.imService.contactUser(friendId);
        if(result){
            return null;
        }
        throw CastException.cast(ErrorCode.ADD_CONTACT_FAILED);

    }


    /**
     * 查询联系人列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @param keyword 关键字
     * @return 联系人分页列表
     */
    @GetMapping("/contacts")
    public PageResult queryContactsList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        return Optional.ofNullable(this.imService.queryContactsList(page, pageSize, keyword))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_CONTACT_FAILED));
    }


    /**
     * 查询消息点赞列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 点赞列表
     */
    @GetMapping("/likes")
    public PageResult queryLikeCommentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

           return Optional.ofNullable(this.imService.queryLikeCommentList(page, pageSize))
                   .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_LIKE_LIST_FAILED));

    }

    /**
     * 查询消息喜欢列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 喜欢列表
     */
    @GetMapping("/loves")
    public PageResult queryLoveCommentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        return Optional.ofNullable(this.imService.queryLoveCommentList(page, pageSize))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_LOVE_LIST_FAILED));

    }

    /**
     * 查询消息评论列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 评论列表
     */
    @GetMapping("/comments")
    public PageResult queryUserCommentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        return Optional.ofNullable(this.imService.queryUserCommentList(page, pageSize))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_COMMENT_LIST_FAILED));

    }



    /**
     * 查询公告列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 公告列表
     */
    @GetMapping("announcements")
    @NoAuthorization
    public PageResult queryMessageAnnouncementList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                   @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        return Optional.ofNullable(this.imService.queryMessageAnnouncementList(page, pageSize))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_ANNOUNCEMENT_FAILED));

    }


}


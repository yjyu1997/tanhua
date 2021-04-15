package top.yusora.tanhua.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.QuanZiService;
import top.yusora.tanhua.server.service.VideoService;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.utils.Cache;

import java.util.Map;
import java.util.Optional;

/**
 * @author heyu
 */
@RestController
@RequestMapping("smallVideos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private QuanZiService quanZiService;

    /**
     * 发布小视频
     *
     * @param picFile   图片文件
     * @param videoFile 视频文件
     * @return 响应体
     */
    @PostMapping
    public Void saveVideo(@RequestParam("videoThumbnail") MultipartFile picFile,
                          @RequestParam("videoFile") MultipartFile videoFile) {

        Boolean result = this.videoService.saveVideo(picFile, videoFile);

        if (result) {
            return null;
        }
        throw CastException.cast(ErrorCode.SAVE_VIDEO_FAILED);

    }

    /**
     * 分页查询小视频列表
     *
     * @param page     当前页数
     * @param pageSize 每页显示条数
     * @return 分页查询结果
     */
    @Cache(time = "30")
    @GetMapping
    public PageResult queryVideoList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        if (page <= 0) {
            page = 1;
        }
        Optional<PageResult> pageResult = Optional.ofNullable(this.videoService.queryVideoList(page, pageSize));
        return pageResult.orElseThrow(() -> CastException.cast(ErrorCode.QUERY_VIDEO_FAILED));
    }


    /**
     * 点赞
     *
     * @param videoId 评论id
     * @return 点赞数
     */
    @PostMapping("{id}/like")
    public Long likeComment(@PathVariable("id") String videoId) {

        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.likeComment(videoId, QuanZiType.VIDEO));

        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.LIKE_OPERATION_FAILED));

    }

    /**
     * 取消点赞
     *
     * @param videoId 视频ID
     * @return 业务是否成功 成功：携带点赞数
     */
    @PostMapping("/{id}/dislike")
    public Long disLikeComment(@PathVariable("id") String videoId) {

        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.disLikeComment(videoId, QuanZiType.VIDEO));
        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.DISLIKE_OPERATION_FAILED));

    }


    /**
     * 查询评论列表
     *
     * @return 业务是否成功 成功：携带评论列表分页结果
     */
    @GetMapping("/{id}/comments")
    public PageResult queryCommentsList(@PathVariable("id") String videoId,
                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        Optional<PageResult> pageResult = Optional.ofNullable(this.quanZiService.queryCommentList(videoId, page, pageSize));
        return pageResult.orElseThrow(() -> CastException.cast(ErrorCode.RESULT_IS_NULL));

    }


    /**
     * 保存评论
     *
     * @param videoId 视频Id
     * @param param   参数：movementId 父动态id  comment：评论内容
     * @return 成功：null
     */
    @PostMapping("/{id}/comments")
    public Void saveComments(@RequestBody Map<String, String> param,
                             @PathVariable("id") String videoId) {

        String content = param.get("comment");
        Boolean result = this.videoService.saveComments(videoId, content);
        if (result) {
            return null;
        }
        throw CastException.cast(ErrorCode.SAVE_COMMENTS_FAILURE);

    }

    /**
     * 给视频中评论点赞
     *
     * @param videoCommentId 视频中的评论id
     * @return 评论点赞数
     */
    @PostMapping("/comments/{id}/like")
    public Long commentsLikeComment(@PathVariable("id") String videoCommentId) {
        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.likeComment(videoCommentId, QuanZiType.COMMENT));

        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.LIKE_OPERATION_FAILED));
    }


    /**
     * 评论取消点赞
     *
     * @param videoCommentId 视频中的评论id
     * @return 评论点赞数
     */
    @PostMapping("/comments/{id}/dislike")
    public Long disCommentsLikeComment(@PathVariable("id") String videoCommentId) {
        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.disLikeComment(videoCommentId,QuanZiType.COMMENT));

        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.DISLIKE_OPERATION_FAILED));
    }


    /**
     * 视频用户关注
     * @param videoUserId 用户Id
     * @return 成功：null
     */
    @PostMapping("/{id}/userFocus")
    public Void saveUserFocusComments(@PathVariable("id") Long videoUserId) {

        Boolean result = this.videoService.followUser(videoUserId);
        if (result) {
            return null;
        }
        throw CastException.cast(ErrorCode.FOLLOW_USER_FAILED);

    }

    /**
     * 取消视频用户关注
     * @param videoUserId 用户Id
     * @return 成功：null
     */
    @PostMapping("/{id}/userUnFocus")
    public Void saveUserUnFocusComments(@PathVariable("id") Long videoUserId) {

        Boolean result = this.videoService.disFollowUser(videoUserId);
        if (result) {
            return null;
        }
        throw CastException.cast(ErrorCode.UNFOLLOW_USER_FAILED);



    }
}
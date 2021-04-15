package top.yusora.tanhua.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.server.service.QuanZiService;
import top.yusora.tanhua.server.vo.PageResult;

import java.util.Map;
import java.util.Optional;


/**
 * 圈子功能中的评论
 * @author heyu
 */
@RestController
@RequestMapping("/comments")
public class QuanZiCommentController {

    @Autowired
    private QuanZiService quanZiService;

    /**
     * 查询评论列表
     *
     * @return 业务是否成功 成功：携带评论列表分页结果
     */
    @GetMapping
    public PageResult queryCommentsList(@RequestParam("movementId") String publishId,
                                                        @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

            Optional<PageResult> pageResult = Optional.ofNullable(this.quanZiService.queryCommentList(publishId, page, pageSize));
            return pageResult.orElseThrow(() -> CastException.cast(ErrorCode.RESULT_IS_NULL));

    }

    /**
     * 保存评论
     * @param param 参数：movementId 父动态id  comment：评论内容
     * @return 成功：null
     */
    @PostMapping
    public Void saveComments(@RequestBody Map<String, String> param) {

            String publishId = param.get("movementId");
            String content = param.get("comment");
            Boolean result = this.quanZiService.saveComments(publishId, content);
            if (result) {
                return null;
            }
            throw CastException.cast(ErrorCode.SAVE_COMMENTS_FAILURE);
    }

    /**
     * 点赞
     *
     * @param commentId 评论id
     * @return 点赞数
     */
    @GetMapping("{id}/like")
    public Long likeComment(@PathVariable("id") String commentId) {

        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.likeComment(commentId, QuanZiType.COMMENT));

        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.LIKE_OPERATION_FAILED));
    }

    /**
     * 取消点赞
     *
     * @param commentId 评论id
     * @return 点赞数
     */
    @GetMapping("{id}/dislike")
    public Long disLikeComment(@PathVariable("id") String commentId) {
        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.disLikeComment(commentId,QuanZiType.COMMENT));

        return likeCount.orElseThrow(() ->  CastException.cast(ErrorCode.DISLIKE_OPERATION_FAILED));


    }

}


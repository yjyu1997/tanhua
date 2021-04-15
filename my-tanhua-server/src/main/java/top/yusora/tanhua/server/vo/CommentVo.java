package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 评论
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo implements Serializable {

    private static final long serialVersionUID = 7390280617872945759L;
    /**
     * @Description 评论id
     */
    private String id;
    /**
     * @Description 头像
     */
    private String avatar;
    /**
     * @Description 昵称
     */
    private String nickname;
    /**
     * @Description 评论
     */
    private String content;
    /**
     * @Description 评论时间: e.g 08:27
     */
    private String createDate;
    /**
     * @Description 点赞数
     */
    private Integer likeCount;
    /**
     * @Description 是否点赞（1是，0否）
     */
    private Integer hasLiked;

}
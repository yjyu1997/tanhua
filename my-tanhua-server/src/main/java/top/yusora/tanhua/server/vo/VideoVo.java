package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoVo implements Serializable {

    private static final long serialVersionUID = -5989602871304937495L;
    /**
     * @Description 主键ID
     */
    private String id;
    /**
     * @Description 用户ID
     */
    private Long userId;
    /**
     * @Description 头像
     */
    private String avatar;
    /**
     * @Description 昵称
     */
    private String nickname;
    /**
     * @Description 封面
     */
    private String cover;
    /**
     * @Description 视频URL
     */
    private String videoUrl;
    /**
     * @Description 签名
     */
    private String signature;
    /**
     * @Description 点赞数量
     */
    private Integer likeCount;
    /**
     * @Description 是否已赞（1是，0否）
     */
    private Integer hasLiked;
    /**
     * @Description 是否关注 （1是，0否）
     */
    private Integer hasFocus;
    /**
     * @Description 评论数量
     */
    private Integer commentCount;
}
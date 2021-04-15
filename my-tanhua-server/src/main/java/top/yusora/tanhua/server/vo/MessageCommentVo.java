package top.yusora.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author heyu
 * 消息功能中评论vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCommentVo implements Serializable {

    private static final long serialVersionUID = 1127659983286751417L;
    /**
     * @Description 评论id
     */
    private String id;
    /**
     * @Description 头像
     *
     */
    private String avatar;
    /**
     * @Description 昵称
     */
    private String nickname;
    /**
     * @Description //格式：2019-09-08 10:07
     */
    private String createDate;

}
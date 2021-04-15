package top.yusora.tanhua.server.vo;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 圈子vo
 * @author heyu 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuanZiVo implements Serializable {

    private static final long serialVersionUID = 5058780930893238578L;
    /**
     * @Description 动态id
     */
    private String id;   
    /**
     * @Description 用户id
     */
    private Long userId;   
    /**
     * @Description 头像
     */
    @Alias("logo")
    private String avatar;   
    /**
     * @Description 昵称
     */
    @Alias("nickName")
    private String nickname;   
    /**
     * @Description 性别 man woman
     */
    private String gender;   
    /**
     * @Description 年龄
     */
    private Integer age;   
    /**
     * @Description 标签
     */
    private String[] tags;   
    /**
     * @Description 文字动态
     */
    private String textContent;   
    /**
     * @Description 图片动态
     */
    private String[] imageContent;   
    /**
     * @Description 距离
     */
    private String distance;   
    /**
     * @Description 发布时间 如: 10分钟前
     */
    private String createDate;   
    /**
     * @Description 点赞数
     */
    private Integer likeCount;   
    /**
     * @Description 评论数
     */
    private Integer commentCount;   
    /**
     * @Description 喜欢数
     */
    private Integer loveCount;   
    /**
     * @Description 是否点赞（1是，0否）
     */
    private Integer hasLiked;   
    /**
     * @Description 是否喜欢（1是，0否）
     */
    private Integer hasLoved;   

}
package top.yusora.tanhua.server.vo;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户信息类 用于我的中心 即时通讯的信息展示
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo implements Serializable {

    private static final long serialVersionUID = 7870133711590124488L;
    /**
     * @Description //用户id
     */
    @Alias("userId")
    private Long id;
    /**
     * @Description //头像
     */
    @Alias("logo")
    private String avatar;
    /**
     * @Description //昵称
     */
    @Alias("nickName")
    private String nickname;
    /**
     * @Description //生日 2019-09-11
     */
    private String birthday;
    /**
     * @Description //年龄
     */
    private String age;
    /**
     * @Description //性别 man woman
     */
    private String gender;
    /**
     * @Description //城市
     */
    private String city;
    /**
     * @Description //学历
     */
    @Alias("edu")
    private String education;
    /**
     * @Description //月收入
     */
    private String income;
    /**
     * @Description //行业
     */
    @Alias("industry")
    private String profession;
    /**
     * @Description //婚姻状态（0未婚，1已婚）
     */
    private Integer marriage;

}
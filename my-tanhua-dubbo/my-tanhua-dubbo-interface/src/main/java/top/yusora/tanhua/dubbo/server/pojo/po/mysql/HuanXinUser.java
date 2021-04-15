package top.yusora.tanhua.dubbo.server.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 环信用户对象
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_huanxin_user")
public class HuanXinUser implements java.io.Serializable{

    private static final long serialVersionUID = -6400630011196593976L;

    /**
     * @Description 主键Id
     */
    @TableId
    private Long id;

    /**
     * @Description  环信 ID ;也就是 IM 用户名的唯一登录账号，长度不可超过64个字符长度
     */
    private String username;
    /**
     * @Description  登录密码，长度不可超过64个字符长度
     */
    private String password;
    /**
     * @Description  昵称（可选），在 iOS Apns 推送时会使用的昵称（仅在推送通知栏内显示的昵称），
     * 并不是用户个人信息的昵称，环信是不保存用户昵称，头像等个人信息的，
     * 需要自己服务器保存并与给自己用户注册的IM用户名绑定，长度不可超过100个字符
     */
    private String nickname;

    /**
     * @Description 用户id
     */
    private Long userId;

    /**
     * @Description 创建时间
     */
    private LocalDateTime created;

    /**
     * @Description 更新时间
     */
    private LocalDateTime updated;

}


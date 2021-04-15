package top.yusora.tanhua.dubbo.server.pojo.po.mysql;



import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.yusora.tanhua.dubbo.server.enums.SexEnum;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BasePojo;


/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserInfo extends BasePojo {

    private static final long serialVersionUID = 1L;

    /**
     * @Description  主键ID
     */
    @TableId
    private Long id;
    
    /**
     * @Description 用户ID
     */
    @TableField("user_id")
    private Long userId; 

    /**
     * @Description 昵称
     */
    private String nickName;

    /**
     * @Description    用户头像
     */
    private String logo;

    /**
     * @Description    用户标签：多个用逗号分隔
     */
    private String tags;

    /**
     * @Description    性别
     */
    private SexEnum sex;

    /**
     * @Description    年龄
     */
    private Integer age;

    /**
     * @Description    学历
     */
    private String edu;

    /**
     * @Description    城市
     */
    private String city;

    /**
     * @Description    生日
     */
    private String birthday;

    /**
     * @Description     封面图片
     */
    private String coverPic;

    /**
     * @Description    行业
     */
    private String industry;

    /**
     * @Description    收入
     */
    private String income;

    /**
     * @Description    婚姻状态
     */
    private String marriage; 

}

package top.yusora.tanhua.dubbo.server.pojo.po.mysql;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BasePojo;


/**
 * @author heyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends BasePojo {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码，json序列化时忽略
     */
    @JsonIgnore
    private String password;

}

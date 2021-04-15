package top.yusora.tanhua.server;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.User;
import top.yusora.tanhua.server.service.IMService;
import top.yusora.tanhua.utils.UserThreadLocal;

@SpringBootTest
public class TestIMService {

    @Autowired
    private IMService imService;

    /**
     * 构造好友数据，为1~99用户构造10个好友
     */
    @Test
    public void testUsers() {
        for (int i = 1; i <= 99; i++) {
            for (int j = 0; j < 10; j++) {
                User user = new User();
                user.setId(Convert.toLong(i));
                UserThreadLocal.set(user.getId());
                this.imService.contactUser(this.getFriendId(user.getId()));
            }
        }
    }

    private Long getFriendId(Long userId) {
        Long friendId = RandomUtil.randomLong(1, 100);
        if (friendId.intValue() == userId.intValue()) {
            getFriendId(userId);
        }
        return friendId;
    }
}
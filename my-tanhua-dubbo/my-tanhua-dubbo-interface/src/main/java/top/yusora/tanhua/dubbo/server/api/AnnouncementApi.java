package top.yusora.tanhua.dubbo.server.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Announcement;

/**
 * @author heyu
 */
public interface AnnouncementApi {

    /**
     * 分页查询公告列表
     * @param page 页数
     * @param pageSize 每页条数
     * @return 公告列表
     */
    IPage<Announcement> queryList(Integer page, Integer pageSize);
}

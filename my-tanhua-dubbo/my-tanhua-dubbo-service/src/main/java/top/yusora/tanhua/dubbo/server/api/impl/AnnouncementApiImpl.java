package top.yusora.tanhua.dubbo.server.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.AnnouncementApi;
import top.yusora.tanhua.dubbo.server.mapper.AnnouncementMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.Announcement;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
@Slf4j
public class AnnouncementApiImpl implements AnnouncementApi {

    @Autowired
    private AnnouncementMapper announcementMapper;
    /**
     * 分页查询公告列表
     *
     * @param page     页数
     * @param pageSize 每页条数
     * @return 公告列表
     */
    @Override
    public IPage<Announcement> queryList(Integer page, Integer pageSize) {

        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created");
        return this.announcementMapper.selectPage(new Page<>(page, pageSize), queryWrapper);
    }
}

package top.yusora.tanhua.dubbo.server.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.dubbo.server.api.BlackListApi;
import top.yusora.tanhua.dubbo.server.mapper.BlackListMapper;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BlackList;

/**
 * @author heyu
 */
@DubboService(version = "1.0.0")
@Service
@Slf4j
public class BlackListApiImpl implements BlackListApi {


    @Autowired
    private BlackListMapper blackListMapper;

    /**
     * 查询黑名单
     *
     * @param userId   用户Id
     * @param page     当前页数
     * @param pageSize 每页条数
     * @return 黑名单分页列表
     */
    @Override
    public IPage<BlackList> queryBlacklist(Long userId, Integer page, Integer pageSize) {
        QueryWrapper<BlackList> wrapper = new QueryWrapper<BlackList>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("created");
        Page<BlackList> pager = new Page<>(page, pageSize);
        return this.blackListMapper.selectPage(pager, wrapper);
    }

    /**
     * 移除黑名单
     *
     * @param userId      用户id
     * @param blackUserId 需移除的黑名单用户id
     * @return 是否移出成功
     */
    @Override
    public Boolean delBlacklist(Long userId, Long blackUserId) {
        QueryWrapper<BlackList> wrapper = new QueryWrapper<BlackList>();
        wrapper.eq("user_id", userId).eq("black_user_id", blackUserId);
        return this.blackListMapper.delete(wrapper) > 0;
    }
}

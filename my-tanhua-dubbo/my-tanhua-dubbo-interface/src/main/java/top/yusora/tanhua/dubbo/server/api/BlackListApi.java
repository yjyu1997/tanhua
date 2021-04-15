package top.yusora.tanhua.dubbo.server.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.BlackList;

/**
 * @author heyu
 */
public interface BlackListApi {

    /**
     * 查询黑名单
     *
     * @param userId 用户Id
     * @param page 当前页数
     * @param pageSize 每页条数
     * @return 黑名单分页列表
     */
    IPage<BlackList> queryBlacklist(Long userId, Integer page, Integer pageSize);


    /**
     * 移除黑名单
     * @param userId 用户id
     * @param blackUserId 需移除的黑名单用户id
     * @return 是否移出成功
     */
    Boolean delBlacklist(Long userId, Long blackUserId);
}

package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Visitors;

import java.util.List;

/**
 * @author heyu
 */
public interface VisitorsApi {

    /**
     * 保存访客数据
     *
     * @param userId 我的id
     * @param visitorUserId 访客id
     * @param from 来源
     * @return 访客记录id
     */
    String saveVisitor(Long userId, Long visitorUserId, String from);

    /**
     * 查询我的访客数据，存在2种情况：
     * 1. 我没有看过我的访客数据，返回前5个访客信息
     * 2. 之前看过我的访客，从上一次查看的时间点往后查询5个访客数据
     *
     * @param userId  我的id
     * @return 访客记录列表
     */
    List<Visitors> queryMyVisitor(Long userId);


    /**
     * 按照时间倒序排序，查询最近的访客信息
     *
     * @param userId 我的id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 访客分页列表
     */
    PageInfo<Visitors> topVisitor(Long userId, Integer page, Integer pageSize);

}


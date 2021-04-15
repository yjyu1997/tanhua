package top.yusora.tanhua.server.service;

import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.server.vo.TodayBest;

/**
 * @author heyu
 */
public interface RecommendUserService {

    /**
     * 根据用户Id查询今日佳人
     * @param userId 用户ID
     * @return 今日佳人vo
     */
    TodayBest queryTodayBest(Long userId);

    /**
     * 根据 用户Id 分页查询 推荐用户 列表
     * @param userId 用户ID
     * @param page 当前页
     * @param pagesize 每页条数
     * @return 推荐用户列表
     */
    PageInfo<RecommendUser> queryRecommendUserList(Long userId, Integer page, Integer pagesize);

    /**
     * 查询推荐好友的缘分值
     *
     * @param userId 好友用户Id
     * @param toUserId 我的用户id
     * @return 缘分值
     */
    Long queryScore(Long userId, Long toUserId);
}

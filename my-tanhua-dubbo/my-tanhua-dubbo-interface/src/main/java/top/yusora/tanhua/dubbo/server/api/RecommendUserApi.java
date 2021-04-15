package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.RecommendUser;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;

import java.util.List;

/**
 * @author heyu
 */
public interface RecommendUserApi {

    /**
     * 查询一位得分最高的推荐用户
     *
     * @param userId 用户Id
     * @return 今日佳人
     */
    RecommendUser queryWithMaxScore(Long userId);

    /**
     * 按照得分倒序
     *
     * @param userId 用户id
     * @param pageNum 当前页数
     * @param pageSize 每页条数
     * @return 推荐用户列表-倒序
     */
    PageInfo<RecommendUser> queryPageInfo(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 查询推荐好友的缘分值
     *
     * @param userId 好友的id
     * @param toUserId 我的id
     * @return 好友的缘分值
     */
    Double queryScore(Long userId, Long toUserId);



    /**
     * 查询探花列表，查询时需要排除喜欢和不喜欢的用户
     *
     * @param userId 用户Id
     * @param count 每次查询数量
     * @return 探花列表
     */
    List<RecommendUser> queryCardList(Long userId, Integer count);
}

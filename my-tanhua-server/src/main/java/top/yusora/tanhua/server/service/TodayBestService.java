package top.yusora.tanhua.server.service;

import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.RecommendUserQueryParam;
import top.yusora.tanhua.server.vo.TodayBest;

/**
 * @author heyu
 */
public interface TodayBestService {
    /**
     * 根据Token查找用户今日佳人
     * @param token JWT token
     * @return 今日佳人 id avatar nickname gender age tags fateValue
     */
    TodayBest queryTodayBest(String token);

    /**
     * 根据用户Id分页查询推荐用户列表
     * @param token JWT token
     * @param queryParam 查询参数
     * @return 查询结果
     */
    PageResult queryRecommendation(String token, RecommendUserQueryParam queryParam);
}

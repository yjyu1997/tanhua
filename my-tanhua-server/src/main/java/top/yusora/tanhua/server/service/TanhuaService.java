package top.yusora.tanhua.server.service;


import top.yusora.tanhua.server.vo.NearUserVo;
import top.yusora.tanhua.server.vo.TodayBest;

import java.util.List;

/**
 * @author heyu
 */
public interface TanhuaService {

    /**
     * 根据用户ID查询相应用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    TodayBest queryUserInfo(Long userId);

    /**
     * 查询陌生人问题
     *
     * @param userId 陌生人Id
     * @return 问题
     */
    String queryQuestion(Long userId);

    /**
     * 回复陌生人问题 （聊一下）
     * @param userId 陌生人ID
     * @param reply 回复内容
     * @return 是否发送成功
     */
    Boolean replyQuestion(Long userId, String reply);

    /**
     * 搜附近
     *
     * @param gender 性别
     * @param distance 距离
     * @return 附近的人列表
     */
    List<NearUserVo> queryNearUser(String gender, String distance);


    /**
     * 查询推荐卡片列表，从推荐列表中随机选取10个用户
     * @return 推荐卡片列表
     */

    List<TodayBest> queryCardsList();

    /**
     * 喜欢:右滑
     *
     * @param likeUserId 对方用户Id
     *
     */
    Boolean likeUser(Long likeUserId);

    /**
     * 不喜欢：左滑
     *
     * @param likeUserId 对方用户id
     */
    Boolean notLikeUser(Long likeUserId);
}

package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.UserLike;

import java.util.List;

public interface UserLikeApi {

    /**
     * 喜欢
     *
     * @param userId 自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    Boolean likeUser(Long userId, Long likeUserId);

    /**
     * 不喜欢
     *
     * @param userId 自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    Boolean notLikeUser(Long userId, Long likeUserId);

    /**
     * 是否喜欢
     *
     * @param userId 自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    Boolean isLike(Long userId, Long likeUserId);

    /**
     * 是否不喜欢
     *
     * @param userId 自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    Boolean isNotLike(Long userId, Long likeUserId);


    /**
     * 是否相互喜欢
     *
     * @param userId 自己的用户id
     * @param likeUserId 对方的用户id
     * @return 业务是否成功
     */
    Boolean isMutualLike(Long userId, Long likeUserId);


    /**
     * 查询喜欢列表
     *
     * @param userId 自己的用户id
     * @return 喜欢列表
     */
    List<Long> queryLikeList(Long userId);

    /**
     * 查询不喜欢列表
     *
     * @param userId 自己的用户id
     * @return 不喜欢列表
     */
    List<Long> queryNotLikeList(Long userId);




    /**
     * 相互喜欢的数量
     * @param userId 用户id
     * @return 相互喜欢的数量
     */
    Long queryMutualLikeCount(Long userId);

    /**
     * 喜欢数
     * @param userId 用户id
     * @return 喜欢数
     */
    Long queryLikeCount(Long userId);

    /**
     * 粉丝数
     * @param userId 用户id
     * @return 粉丝数
     */
    Long queryFanCount(Long userId);



    /**
     * 分页查询相互喜欢列表
     *
     * @param userId 用户Id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 相互喜欢分页列表
     */
    PageInfo<UserLike> queryMutualLikeList(Long userId, Integer page, Integer pageSize);

    /**
     * 查询我喜欢的列表
     *
     * @param userId 用户Id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 我喜欢的分页列表
     */
    PageInfo<UserLike> queryLikeList(Long userId, Integer page, Integer pageSize);

    /**
     * 查询粉丝列表
     *
     * @param userId 用户Id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 粉丝分页列表
     */
    PageInfo<UserLike> queryFanList(Long userId, Integer page, Integer pageSize);

}
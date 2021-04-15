package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Comment;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Publish;

/**
 * @author heyu
 */
public interface QuanZiApi {

    /**
     * 分页查询好友动态
     *
     * @param userId 用户id
     * @param page 当前页数
     * @param pageSize 每一页查询的数据条数
     * @return 好友动态列表
     */
    PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize);

    /**
     * 发布动态
     *
     * @param publish 动态内容
     * @return 发布成功返回动态id
     */
    String savePublish(Publish publish);


    /**
     * 分页查询推荐动态
     *
     * @param userId 用户id
     * @param page 当前页数
     * @param pageSize 每一页查询的数据条数
     * @return 推荐动态列表
     */
    PageInfo<Publish> queryRecommendPublishList(Long userId, Integer page, Integer pageSize);

    /**
     * 根据主键id查询动态
     *
     * @param id 动态id
     * @return 动态数据
     */
    Publish queryPublishById(String id);

    /**
     * 点赞
     *
     * @param userId 用户ID
     * @param publishId 动态ID
     * @return 业务是否成功
     */
    Boolean likeComment(Long userId, String publishId, QuanZiType type);

    /**
     * 取消点赞
     *
     * @param userId 当前操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    Boolean disLikeComment(Long userId, String publishId);

    /**
     * 查询点赞数
     *
     * @param publishId 动态Id
     * @return 点赞数
     */
    Long queryLikeCount(String publishId);

    /**
     * 查询用户是否点赞该动态
     *
     * @param userId 操作用户Id
     * @param publishId 动态Id
     * @return true：已点赞 false：未点赞
     */
    Boolean queryUserIsLike(Long userId, String publishId);

//---------------------------以下喜欢功能只在推荐动态中实现----------------------

    /**
     * 喜欢
     *
     * @param userId 操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    Boolean loveComment(Long userId, String publishId,QuanZiType type);

    /**
     * 取消喜欢
     *
     * @param userId 操作用户Id
     * @param publishId 动态Id
     * @return 业务是否成功
     */
    Boolean disLoveComment(Long userId, String publishId);

    /**
     * 查询喜欢数
     *
     * @param publishId 动态Id
     * @return 喜欢数
     */
    Long queryLoveCount(String publishId);

    /**
     * 查询用户是否喜欢该动态
     *
     * @param userId 操作用户Id
     * @param publishId 动态Id
     * @return true：已喜欢 false：未喜欢
     */
    Boolean queryUserIsLove(Long userId, String publishId);

    //----------------------------------------------------------------------------

    /**
     * 查询评论列表
     * @param publishId 动态ID
     * @param page 页数
     * @param pageSize 每页条数
     * @return 评论列表
     */
    PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize);

    /**
     * 发表评论
     *
     * @param userId 发表评论者id
     * @param publishId 当前动态id
     * @param content 评论内容
     * @param type 父圈子类型 动态/评论/小视频
     * @return 业务是否成功
     */
    Boolean saveComment(Long userId, String publishId, String content,QuanZiType type);


    /**
     * 查询评论数
     *
     * @param publishId 动态Id
     * @return 评论数
     */
    Long queryCommentCount(String publishId);


    /**
     * 查询对我的点赞消息列表
     * @param userId 用户Id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 点赞消息列表
     */
    PageInfo<Comment> queryLikeCommentListByUser(Long userId, Integer page, Integer pageSize);

    /**
     * 查询对我的喜欢消息列表
     * @param userId 用户Id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 喜欢消息列表
     */
    PageInfo<Comment> queryLoveCommentListByUser(Long userId, Integer page, Integer pageSize);

    /**
     * 查询对我的评论消息列表
     * @param userId 用户Id
     * @param page 页数
     * @param pageSize 每页条数
     * @return 评论消息列表
     */
    PageInfo<Comment> queryCommentListByUser(Long userId, Integer page, Integer pageSize);


    /**
     * 查询相册表
     *
     * @param userId 用户Id
     * @param page 当前页数
     * @param pageSize 每页条数
     * @return 该用户发布的动态列表
     */
    PageInfo<Publish> queryAlbumList(Long userId, Integer page, Integer pageSize);
}

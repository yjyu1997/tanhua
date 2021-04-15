package top.yusora.tanhua.server.service;

import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.UserInfoVo;

/**
 * @author heyu
 */
public interface IMService {
    /**
     * 根据环信用户名检索用户信息
     * @param userName 环信用户名
     * @return 用户基本信息
     */
    UserInfoVo queryUserInfoByUserName(String userName);

    /**
     * 添加好友
     * @param friendId 好友Id
     * @return 业务是否成功
     */
    boolean contactUser(Long friendId);

    /**
     * 查询联系人列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @param keyword 关键字
     * @return 联系人分页列表
     */
    PageResult queryContactsList(Integer page, Integer pageSize, String keyword);

    /**
     * 查询消息点赞列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 点赞列表
     */
    PageResult queryLikeCommentList(Integer page, Integer pageSize);

    /**
     * 查询消息喜欢列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 喜欢列表
     */
    PageResult queryLoveCommentList(Integer page, Integer pageSize);



    /**
     * 查询消息评论列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 评论列表
     */
    PageResult queryUserCommentList(Integer page, Integer pageSize);

    /**
     * 查询公告列表
     *
     * @param page 页数
     * @param pageSize 每页条数
     * @return 公告列表
     */
    PageResult queryMessageAnnouncementList(Integer page, Integer pageSize);
    /**
     * 删除好友
     *
     * @param userId 好友id
     */
    void removeUser(Long userId);
}

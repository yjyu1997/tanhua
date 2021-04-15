package top.yusora.tanhua.dubbo.server.api;

import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Video;

/**
 * @author heyu
 */
public interface VideoApi {

    /**
     * 保存小视频
     *
     * @param video 小视频
     * @return 保存成功后，返回视频id
     */
    String saveVideo(Video video);

    /**
     * 分页查询小视频列表，按照时间倒序排序
     *
     * @param userId 用户ID
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @return 小视频列表
     */
    PageInfo<Video> queryVideoList(Long userId, Integer page, Integer pageSize);

    /**
     * 根据id查询视频对象
     *
     * @param videoId 小视频id
     * @return 小视频
     */
    Video queryVideoById(String videoId);


    /**
     * 关注用户
     *
     * @param userId 当前用户
     * @param followUserId 关注的目标用户
     * @return 业务是否成功
     */
    Boolean followUser(Long userId, Long followUserId);

    /**
     * 取消关注用户
     *
     * @param userId 当前用户
     * @param followUserId 关注的目标用户
     * @return 业务是否成功
     */
    Boolean disFollowUser(Long userId, Long followUserId);

    /**
     * 查询用户是否关注某个用户
     *
     * @param userId 当前用户
     * @param followUserId 关注的目标用户
     * @return 业务是否成功
     */
    Boolean isFollowUser(Long userId, Long followUserId);

}


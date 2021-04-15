package top.yusora.tanhua.server.service;

import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.server.vo.PageResult;

/**
 * @author heyu
 */
public interface VideoService {

    /**
     * 发布小视频
     *
     * @param picFile 图片文件
     * @param videoFile 视频文件
     * @return 业务是否成功
     */
    Boolean saveVideo(MultipartFile picFile, MultipartFile videoFile);

    /**
     * 分页查询小视频列表
     * - 优先查询推荐列表
     * - 没有或已经显示完则查询数据库
     * @param page 当前页面
     * @param pageSize 每页显示条数
     * @return 小视频列表结果集
     */
    PageResult queryVideoList(Integer page, Integer pageSize);

    /**
     * 保存评论
     * @param videoId 父动态id
     * @param content 评论内容
     * @return 业务是否成功
     */
    Boolean saveComments(String videoId, String content);

    /**
     * 视频用户关注
     * @param videoUserId 用户Id
     * @return 业务是否成功
     */
    Boolean followUser(Long videoUserId);

    /**
     * 取消视频用户关注
     * @param videoUserId 用户Id
     * @return 业务是否成功
     */
    Boolean disFollowUser(Long videoUserId);
}

package top.yusora.tanhua.server.service;


import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.QuanZiVo;
import top.yusora.tanhua.server.vo.VisitorsVo;

import java.util.List;

/**
 * @author heyu
 */
public interface QuanZiService {

    /**
     * 查询好友动态
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @param token JWT token 包含用户ID 过期时间
     * @return PageResult 圈子结果集
     */
    PageResult queryPublishList(Integer page, Integer pageSize, String token);

    /**
     * 发布动态
     * @param textContent 文字内容
     * @Nullable @param location 位置
     * @Nullable @param latitude 纬度
     * @Nullable @param longitude 经度
     * @Nullable @param multipartFile 图片文件
     * @return PublishId
     */

    String savePublish(String textContent, @Nullable String location, @Nullable String latitude,
                       @Nullable String longitude, @Nullable MultipartFile[] multipartFile);


    /**
     * 查询推荐动态
     *
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @return 推荐动态结果
     */
    PageResult queryRecommendPublishList(Integer page, Integer pageSize);


    /**
     * 点赞
     *
     * @param publishId 动态ID
     * @param type 父圈子类型 动态/评论/小视频 用于查询填入发布者id
     * @return 业务成功: 点赞数  业务失败：null
     */
    Long likeComment(String publishId, QuanZiType type);


    /**
     * 取消点赞
     *
     * @param publishId 动态ID
     * @return 业务是否成功 业务成功: 点赞数  业务失败：null
     */
    Long disLikeComment(String publishId, QuanZiType type);

    /**
     * 喜欢
     * @param publishId 动态ID
     * @param type 父圈子类型 动态/评论/小视频 用于查询填入发布者id
     * @return 业务是否成功 业务成功: 喜欢数  业务失败：null
     */
    Long loveComment(String publishId,QuanZiType type);


    /**
     * 取消喜欢
     * @param publishId 动态ID
     * @return 业务是否成功 业务成功: 喜欢数  业务失败：null
     */
    Long disLoveComment(String publishId);


    /**
     * 查询单条动态信息
     * 用户点击评论时需要查询单条动态详情
     * @param publishId 动态id
     * @return QuanziVo 动态数据
     */
    QuanZiVo queryById(String publishId);

    /**
     * 分页查询评论列表
     * @param publishId 父动态id
     * @param page 当前页数
     * @param pageSize 每页条数
     * @return 分页结果列表
     */
    PageResult queryCommentList(String publishId, Integer page, Integer pageSize);

    /**
     * 保存评论
     * @param publishId 父动态id
     * @param content 评论内容
     * @return 业务是否成功
     */
    Boolean saveComments(String publishId, String content);


    /**
     * 指定用户的所有动态
     * @param page 页数
     * @param pageSize 每页条数
     * @param userId 用户Id
     * @return 动态列表
     */
    PageResult queryAlbumList(Long userId, Integer page, Integer pageSize);

    /**
     * 谁看过我
     *
     * @return 访客数据列表
     */
    List<VisitorsVo> queryVisitorsList();
}

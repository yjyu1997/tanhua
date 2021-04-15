package top.yusora.tanhua.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.dubbo.server.api.QuanZiApi;
import top.yusora.tanhua.dubbo.server.api.VideoApi;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.dubbo.server.pojo.dto.PageInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mysql.UserInfo;
import top.yusora.tanhua.dubbo.server.pojo.po.mongodb.Video;
import top.yusora.tanhua.server.service.UserInfoService;
import top.yusora.tanhua.server.service.VideoMQService;
import top.yusora.tanhua.server.service.VideoService;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.VideoVo;
import top.yusora.tanhua.service.PicUploadService;
import top.yusora.tanhua.utils.UserThreadLocal;
import top.yusora.tanhua.vo.PicUploadResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author heyu
 */
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private PicUploadService picUploadService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    protected FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @DubboReference(version = "1.0.0")
    private VideoApi videoApi;

    @DubboReference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private VideoMQService videoMQService;


    /**
     * 发布小视频
     *
     * @param picFile 图片文件
     * @param videoFile 视频文件
     * @return 业务是否成功
     */
    @Override
    public Boolean saveVideo(MultipartFile picFile, MultipartFile videoFile) {
        Long userId = UserThreadLocal.get();

        Video video = new Video();
        video.setUserId(userId);
        //默认公开
        video.setSeeType(1);

        try {
            //上传封面图片
            PicUploadResult picUploadResult = this.picUploadService.upload(picFile);
            //图片路径
            video.setPicUrl(picUploadResult.getName());

            //上传视频
            StorePath storePath = storageClient.uploadFile(videoFile.getInputStream(),
                    videoFile.getSize(),
                    StrUtil.subAfter(videoFile.getOriginalFilename(), '.', true),
                    null);

            //设置视频url
            video.setVideoUrl(fdfsWebServer.getWebServerUrl() + storePath.getFullPath());

            String videoId = this.videoApi.saveVideo(video);

            if(StrUtil.isNotEmpty(videoId)){
                //发送消息
                this.videoMQService.videoMsg(videoId);
            }

            return StrUtil.isNotEmpty(videoId);
        } catch (Exception e) {
            log.error("发布小视频失败！file = " + picFile.getOriginalFilename() , e);
        }

        return false;
    }


    /**
     * 分页查询小视频列表
     * - 优先查询推荐列表
     * - 没有或已经显示完则查询数据库
     *
     * @param page     当前页面
     * @param pageSize 每页显示条数
     * @return 小视频列表结果集
     */
    @Override
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        //初始化PageResult
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //RPC调用，获取小视频数据
        Optional<PageInfo<Video>> pageInfo = Optional.ofNullable(this.videoApi.queryVideoList(userId, page, pageSize));
        List<Video> records = pageInfo.map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        //获取用户信息Map
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"));

        //------------------转换成VideoVo对象------------------
        List<VideoVo> videoVoList = new ArrayList<>();
        records.forEach(record -> {
            VideoVo videoVo = new VideoVo();

            videoVo.setUserId(record.getUserId());
            videoVo.setCover(record.getPicUrl());
            videoVo.setVideoUrl(record.getVideoUrl());
            videoVo.setId(record.getId().toHexString());
            videoVo.setSignature("我就是我~"); //TODO 签名

            //评论数
            videoVo.setCommentCount(Convert.toInt(this.quanZiApi.queryCommentCount(videoVo.getId())));
            //TODO 是否关注
            videoVo.setHasFocus(this.videoApi.isFollowUser(userId, videoVo.getUserId()) ? 1 : 0);
            //是否点赞（1是，0否）
            videoVo.setHasLiked(this.quanZiApi.queryUserIsLike(userId, videoVo.getId()) ? 1 : 0);
            //点赞数
            videoVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(videoVo.getId())));

            //--------------填充用户基本信息-------------------
            UserInfo userInfo = userInfoMap.get(record.getUserId());
            videoVo.setNickname(userInfo.getNickName());
            videoVo.setAvatar(userInfo.getLogo());

            videoVoList.add(videoVo);
        });

        pageResult.setItems(videoVoList);
        return pageResult;
    }

    /**
     * 保存评论
     *
     * @param videoId 父视频id
     * @param content 评论内容
     * @return 业务是否成功
     */
    @Override
    public Boolean saveComments(String videoId, String content) {

        //获得用户上下文
        Long userId = UserThreadLocal.get();

        Boolean result = Optional.ofNullable(this.quanZiApi.saveComment(userId, videoId, content, QuanZiType.VIDEO))
                .orElse(false);

        if(result){
            //发送消息
            this.videoMQService.commentVideoMsg(videoId);
        }
        return result;
    }

    /**
     * 视频用户关注
     *
     * @param videoUserId 视频用户Id
     * @return 业务是否成功
     */
    @Override
    public Boolean followUser(Long videoUserId) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.videoApi.followUser(userId,videoUserId))
                .orElse(false);
    }

    /**
     * 取消视频用户关注
     *
     * @param videoUserId 视频用户Id
     * @return 业务是否成功
     */
    @Override
    public Boolean disFollowUser(Long videoUserId) {
        //获取用户上下文
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.videoApi.disFollowUser(userId,videoUserId))
                .orElse(false);
    }


}
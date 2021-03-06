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
     * ???????????????
     *
     * @param picFile ????????????
     * @param videoFile ????????????
     * @return ??????????????????
     */
    @Override
    public Boolean saveVideo(MultipartFile picFile, MultipartFile videoFile) {
        Long userId = UserThreadLocal.get();

        Video video = new Video();
        video.setUserId(userId);
        //????????????
        video.setSeeType(1);

        try {
            //??????????????????
            PicUploadResult picUploadResult = this.picUploadService.upload(picFile);
            //????????????
            video.setPicUrl(picUploadResult.getName());

            //????????????
            StorePath storePath = storageClient.uploadFile(videoFile.getInputStream(),
                    videoFile.getSize(),
                    StrUtil.subAfter(videoFile.getOriginalFilename(), '.', true),
                    null);

            //????????????url
            video.setVideoUrl(fdfsWebServer.getWebServerUrl() + storePath.getFullPath());

            String videoId = this.videoApi.saveVideo(video);

            if(StrUtil.isNotEmpty(videoId)){
                //????????????
                this.videoMQService.videoMsg(videoId);
            }

            return StrUtil.isNotEmpty(videoId);
        } catch (Exception e) {
            log.error("????????????????????????file = " + picFile.getOriginalFilename() , e);
        }

        return false;
    }


    /**
     * ???????????????????????????
     * - ????????????????????????
     * - ??????????????????????????????????????????
     *
     * @param page     ????????????
     * @param pageSize ??????????????????
     * @return ????????????????????????
     */
    @Override
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        //?????????PageResult
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //RPC??????????????????????????????
        Optional<PageInfo<Video>> pageInfo = Optional.ofNullable(this.videoApi.queryVideoList(userId, page, pageSize));
        List<Video> records = pageInfo.map(PageInfo::getRecords).orElse(null);
        if(CollUtil.isEmpty(records)){
            return pageResult;
        }

        //??????????????????Map
        final Map<Long, UserInfo> userInfoMap = this.userInfoService.getUserInfoMap(CollUtil.getFieldValues(records, "userId"));

        //------------------?????????VideoVo??????------------------
        List<VideoVo> videoVoList = new ArrayList<>();
        records.forEach(record -> {
            VideoVo videoVo = new VideoVo();

            videoVo.setUserId(record.getUserId());
            videoVo.setCover(record.getPicUrl());
            videoVo.setVideoUrl(record.getVideoUrl());
            videoVo.setId(record.getId().toHexString());
            videoVo.setSignature("????????????~"); //TODO ??????

            //?????????
            videoVo.setCommentCount(Convert.toInt(this.quanZiApi.queryCommentCount(videoVo.getId())));
            //TODO ????????????
            videoVo.setHasFocus(this.videoApi.isFollowUser(userId, videoVo.getUserId()) ? 1 : 0);
            //???????????????1??????0??????
            videoVo.setHasLiked(this.quanZiApi.queryUserIsLike(userId, videoVo.getId()) ? 1 : 0);
            //?????????
            videoVo.setLikeCount(Convert.toInt(this.quanZiApi.queryLikeCount(videoVo.getId())));

            //--------------????????????????????????-------------------
            UserInfo userInfo = userInfoMap.get(record.getUserId());
            videoVo.setNickname(userInfo.getNickName());
            videoVo.setAvatar(userInfo.getLogo());

            videoVoList.add(videoVo);
        });

        pageResult.setItems(videoVoList);
        return pageResult;
    }

    /**
     * ????????????
     *
     * @param videoId ?????????id
     * @param content ????????????
     * @return ??????????????????
     */
    @Override
    public Boolean saveComments(String videoId, String content) {

        //?????????????????????
        Long userId = UserThreadLocal.get();

        Boolean result = Optional.ofNullable(this.quanZiApi.saveComment(userId, videoId, content, QuanZiType.VIDEO))
                .orElse(false);

        if(result){
            //????????????
            this.videoMQService.commentVideoMsg(videoId);
        }
        return result;
    }

    /**
     * ??????????????????
     *
     * @param videoUserId ????????????Id
     * @return ??????????????????
     */
    @Override
    public Boolean followUser(Long videoUserId) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.videoApi.followUser(userId,videoUserId))
                .orElse(false);
    }

    /**
     * ????????????????????????
     *
     * @param videoUserId ????????????Id
     * @return ??????????????????
     */
    @Override
    public Boolean disFollowUser(Long videoUserId) {
        //?????????????????????
        Long userId = UserThreadLocal.get();
        return Optional.ofNullable(this.videoApi.disFollowUser(userId,videoUserId))
                .orElse(false);
    }


}
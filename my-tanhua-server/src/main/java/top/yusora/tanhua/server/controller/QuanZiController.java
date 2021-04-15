package top.yusora.tanhua.server.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.constant.ErrorCode;
import top.yusora.tanhua.dubbo.server.enums.QuanZiType;
import top.yusora.tanhua.exception.CastException;
import top.yusora.tanhua.exception.ProjectException;
import top.yusora.tanhua.server.service.QuanZiService;
import top.yusora.tanhua.server.vo.PageResult;
import top.yusora.tanhua.server.vo.QuanZiVo;
import top.yusora.tanhua.server.vo.VisitorsVo;
import top.yusora.tanhua.utils.Cache;
import top.yusora.tanhua.utils.NoCommonResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author heyu
 */
@Slf4j
@RestController
@RequestMapping("/movements")
public class QuanZiController {

    @Autowired
    private QuanZiService quanZiService;

    /**
     * 查询好友动态
     *
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @return PageResult 圈子结果集
     */
    @Cache(time = "30")
    @GetMapping
    public PageResult queryPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                       @RequestHeader("Authorization") String token) {
        return this.quanZiService.queryPublishList(page, pageSize, token);
    }

    /**
     * 发布动态
     * @param textContent 文字内容
     * @Nullable @param location 位置
     * @Nullable @param latitude 纬度
     * @Nullable @param longitude 经度
     * @Nullable @param multipartFile 图片文件
     * @return 发布状态
     */
    @PostMapping
    public Void savePublish(@RequestParam("textContent") String textContent,
                                            @RequestParam(value = "location", required = false) String location,
                                            @RequestParam(value = "latitude", required = false) String latitude,
                                            @RequestParam(value = "longitude", required = false) String longitude,
                                            @RequestParam(value = "imageContent", required = false) MultipartFile[] multipartFile) {

        //1.调用QuanZiService

        String publishId = this.quanZiService.savePublish(textContent, location, latitude, longitude, multipartFile);
        if (StrUtil.isNotEmpty(publishId)) {
            //publishId不为空 成功 返回
            return null;
        }
        throw CastException.cast(ErrorCode.SAVE_PUBLISH_FAILED);

    }


    /**
     * 查询推荐动态
     *
     * @param page 当前页数
     * @param pageSize 每页显示条数
     * @return 推荐动态结果
     */
    @GetMapping("recommend")
    public PageResult queryRecommendPublishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        return this.quanZiService.queryRecommendPublishList(page, pageSize);
    }


    /**
     * 点赞
     *
     * @param publishId 动态ID
     * @return 业务是否成功 成功：携带点赞数
     */
    @GetMapping("/{id}/like")
    public Long likeComment(@PathVariable("id") String publishId) {

        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.likeComment(publishId, QuanZiType.PUBLISH));
        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.LIKE_OPERATION_FAILED));
    }


    /**
     * 取消点赞
     *
     * @param publishId 动态ID
     * @return 业务是否成功 成功：携带点赞数
     */
    @GetMapping("/{id}/dislike")
    public Long disLikeComment(@PathVariable("id") String publishId) {

        Optional<Long> likeCount = Optional.ofNullable(this.quanZiService.disLikeComment(publishId,QuanZiType.PUBLISH));
        return likeCount.orElseThrow(() -> CastException.cast(ErrorCode.DISLIKE_OPERATION_FAILED));

    }


    /**
     * 喜欢 只在推荐中操作
     *
     * @param publishId 动态ID
     * @return 业务是否成功 成功：携带喜欢数
     */
    @GetMapping("/{id}/love")
    public Long loveComment(@PathVariable("id") String publishId) {

        Optional<Long> loveCount = Optional.ofNullable(this.quanZiService.loveComment(publishId,QuanZiType.PUBLISH));
        return loveCount.orElseThrow(() -> CastException.cast(ErrorCode.LOVE_OPERATION_FAILED));

    }

    /**
     * 取消喜欢
     *
     * @param publishId 动态ID
     * @return 业务是否成功 成功：携带喜欢数
     */
    @GetMapping("/{id}/unlove")
    public Long disLoveComment(@PathVariable("id") String publishId) {

        Optional<Long> loveCount = Optional.ofNullable(this.quanZiService.disLoveComment(publishId));
        return loveCount.orElseThrow(() -> CastException.cast(ErrorCode.DISLOVE_OPERATION_FAILED));

    }


    /**
     * 查询单条动态信息
     * 用户点击评论时需要查询单条动态详情
     * @param publishId 动态id
     * @return QuanziVo 动态数据
     */
    @GetMapping("/{id}")
    public QuanZiVo queryById(@PathVariable("id") String publishId) {

        Optional<QuanZiVo> movements = Optional.ofNullable(this.quanZiService.queryById(publishId));

        return movements.orElseThrow(() -> CastException.cast(ErrorCode.QUERY_PUBLISH_FAILED));
    }

    /**
     * 指定用户的所有动态
     * @param page 页数
     * @param pageSize 每页条数
     * @param userId 用户Id
     * @return 动态列表
     */
    @GetMapping("all")
    public PageResult queryAlbumList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                     @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(value = "userId") Long userId) {

        return Optional.ofNullable(this.quanZiService.queryAlbumList(userId, page, pageSize))
                .orElseThrow(() -> CastException.cast(ErrorCode.QUERY_ALBUMS_FAILED));

    }

    /**
     * 谁看过我
     *
     * @return 访客数据列表
     */
    @GetMapping("visitors")
    public List<VisitorsVo> queryVisitorsList(){

        List<VisitorsVo> visitorsVos = this.quanZiService.queryVisitorsList();
        log.info("谁看过我{}", visitorsVos);
        return visitorsVos;
    }
}


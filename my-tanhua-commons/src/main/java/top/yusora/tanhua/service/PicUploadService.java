package top.yusora.tanhua.service;

import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.vo.PicUploadResult;

/**
 * @author heyu
 */
public interface PicUploadService {

    /**
     * 图片上传服务
     * @param uploadFile 上传图片
     * @return PictureUploadResult 成功：uid: 毫秒值, name: oss文件路径, status: done
     *                             失败: status: error
     */
    PicUploadResult upload(MultipartFile uploadFile);


}

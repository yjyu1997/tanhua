package top.yusora.tanhua.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.vo.PicUploadResult;
import top.yusora.tanhua.service.impl.PicUploadServiceImpl;

/**
 * @author heyu
 */
@RestController
@RequestMapping("/pic/upload")
public class PicUploadController {
    @Autowired
    private PicUploadServiceImpl picUploadService;

    @PostMapping
    public PicUploadResult upload(@RequestParam("file") MultipartFile multipartFile){
        return this.picUploadService.upload(multipartFile);
    }
}

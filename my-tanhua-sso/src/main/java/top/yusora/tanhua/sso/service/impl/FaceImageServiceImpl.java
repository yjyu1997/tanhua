package top.yusora.tanhua.sso.service.impl;

import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.DetectFaceRequest;
import com.aliyun.facebody20191230.models.DetectFaceResponse;

import com.aliyun.facebody20191230.models.DetectFaceResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yusora.tanhua.sso.service.FaceImageService;
import top.yusora.tanhua.utils.ExceptionsUtil;
import java.util.Optional;



/**
 * @author heyu
 */
@Service
@Slf4j
public class FaceImageServiceImpl implements FaceImageService {
    @Autowired
    Client faceImageClient;

    /**
     * 人像识别
     * @param ossImageUrl oss文件地址
     * @return 是否为人像
     */
    @Override
    public boolean checkIsPortrait(String ossImageUrl){
        DetectFaceRequest req = new DetectFaceRequest()
                .setImageURL(ossImageUrl);
        try{
            Integer faceCount = Optional.ofNullable(faceImageClient.detectFace(req)).map(DetectFaceResponse::getBody)
                    .map(DetectFaceResponseBody::getData)
                    .map(DetectFaceResponseBody.DetectFaceResponseBodyData::getFaceCount)
                    .orElse(-1);
            return faceCount > 0;
        }catch (Exception e) {
            log.error("人脸识别失败，失败原因{}", ExceptionsUtil.getStackTraceAsString(e));
        }
        return false;
    }
}

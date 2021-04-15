package top.yusora.tanhua.sso;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.DetectFaceRequest;
import com.aliyun.facebody20191230.models.DetectFaceResponse;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.yusora.tanhua.sso.service.FaceImageService;
import top.yusora.tanhua.sso.service.impl.FaceImageServiceImpl;

@SpringBootTest
public class FaceImageTest {

    @Autowired
    Client faceImageClient;

    @Autowired
    FaceImageService faceImageService;

    @Test
    public void testDetectFace() throws Exception {
        System.out.println("--------  人脸检测定位 --------------");
        DetectFaceRequest req = new DetectFaceRequest()
                .setImageURL("https://itcast-tanhua.oss-cn-shanghai.aliyuncs.com/images/2019/07/24/1563978726513810.png");

        DetectFaceResponse resp = faceImageClient.detectFace(req);

        System.out.println(JSONObject.toJSONString(resp.getBody().getData()));
    }

    @Test
    public void testDetectFaceService(){
        System.out.println("--------  人脸检测定位 --------------");
        //非人脸
        //String url = "https://itcast-tanhua.oss-cn-shanghai.aliyuncs.com/1563441566007.png";
        //人脸
        String url = "https://itcast-tanhua.oss-cn-shanghai.aliyuncs.com/images/2019/07/24/1563978726513810.png";

        System.out.println(faceImageService.checkIsPortrait(url));
    }
}

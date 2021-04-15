package top.yusora.tanhua.sso.service;

public interface FaceImageService {

    /**
     * 人像识别
     * @param ossImageUrl oss文件地址
     * @return 是否为人像
     */
    boolean checkIsPortrait(String ossImageUrl);
}

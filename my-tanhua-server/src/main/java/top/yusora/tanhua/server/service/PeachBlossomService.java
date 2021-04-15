package top.yusora.tanhua.server.service;

import org.springframework.web.multipart.MultipartFile;
import top.yusora.tanhua.server.vo.SoundVo;

import java.io.IOException;

/**
 * @author heyu
 */
public interface PeachBlossomService {
    /**
     * 发送声音
     * @param soundFile  声音
     * @return 是否发送成功
     * @throws IOException io异常
     */
    Boolean sendSound(MultipartFile soundFile) throws IOException;

    /**
     * 获取声音
     * @return 声音vo
     */
    SoundVo getSound();
}

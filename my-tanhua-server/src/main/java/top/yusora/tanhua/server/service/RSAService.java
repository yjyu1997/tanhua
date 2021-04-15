package top.yusora.tanhua.server.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author heyu
 */
@Service
public class RSAService {
    @Value("${tanhua.rsa.dir}")
    private String rsaDir;

    private String publicKey;

    @PostConstruct
    public void init() {
        String publicKeyFile = rsaDir + File.separator + "rsa.pub";


        if (FileUtil.exist(publicKeyFile)) {
            //公钥文件存在，读取该文件，载入到内存中，方便后续的使用
            this.publicKey = FileUtil.readString(publicKeyFile, CharsetUtil.CHARSET_UTF_8);
        }else {
            //公钥不存在，抛出异常
            throw new RuntimeException(StrUtil.format("公钥文件{}不存在！", publicKeyFile));
        }
    }

    public String getPublicKey() {
        return publicKey;
    }
}

package top.yusora.tanhua.sso.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * RSA文件生成等业务逻辑实现
 * @author heyu
 */
@Service
@Data
public class RSAService {

    @Value("${tanhua.rsa.dir}")
    private String rsaDir;

    private String privateKey;
    private String publicKey;

    /**
     * 初始化操作，如果检测本地目录中没有rsa文件则生成该文件
     */
    @PostConstruct
    public void init() {
        String privateKeyFile = rsaDir + File.separator + "rsa";
        String publicKeyFile = rsaDir + File.separator + "rsa.pub";

        if (FileUtil.exist(privateKeyFile)) {
            //私钥文件存在，读取该文件，载入到内存中，方便后续的使用
            this.privateKey = FileUtil.readString(privateKeyFile, CharsetUtil.CHARSET_UTF_8);
        }

        if (FileUtil.exist(publicKeyFile)) {
            //公钥文件存在，读取该文件，载入到内存中，方便后续的使用
            this.publicKey = FileUtil.readString(publicKeyFile, CharsetUtil.CHARSET_UTF_8);
        }

        if (StrUtil.isAllEmpty(privateKey, publicKey)) {
            //公钥和私钥都没有，生成rsa文件
            RSA rsa = new RSA();

            //写数据到私钥文件
            FileUtil.writeString(rsa.getPrivateKeyBase64(), privateKeyFile, CharsetUtil.CHARSET_UTF_8);
            //写数据到公钥文件
            FileUtil.writeString(rsa.getPublicKeyBase64(), publicKeyFile, CharsetUtil.CHARSET_UTF_8);
        }
    }

}
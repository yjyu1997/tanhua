package top.yusora.tanhua.sso;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.junit.Assert;
import org.junit.Test;

public class TestRSA {

    @Test
    public void testRSA() {
        RSA rsa = new RSA();

        // //获得私钥
        // rsa.getPrivateKey()
        System.out.println("私钥: " + rsa.getPrivateKeyBase64());
        // //获得公钥
        // rsa.getPublicKey()
        System.out.println("公钥: " + rsa.getPublicKeyBase64());

        //公钥加密，私钥解密
        byte[] encrypt = rsa.encrypt(StrUtil.bytes("黑马程序员", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
        System.out.println(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
        Assert.assertEquals("黑马程序员", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));

        //私钥加密，公钥解密
        byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("黑马程序员", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
        byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
        System.out.println(StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
        Assert.assertEquals("黑马程序员", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));

    }
}
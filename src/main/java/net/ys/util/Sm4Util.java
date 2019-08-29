package net.ys.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * 国密SM4加解密工具，对标对称加密(AES/DES)
 * User: NMY
 * Date: 19-8-29
 */
public class Sm4Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "SM4";
    /**
     * 加密算法/分组加密模式/分组填充方式
     * PKCS5Padding:以8个字节为一组进行加密
     * 定义分组加密模式使用：PKCS5Padding
     */
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    //128-32位16进制：256-64位16进制
    public static final int DEFAULT_KEY_SIZE = 128;

    /**
     * 生成ECB暗号
     *
     * @param algorithmName 算法名称
     * @param mode          模式
     * @param key
     * @return
     * @throws Exception
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    /**
     * 生成32位16进制秘钥
     *
     * @return
     * @throws Exception
     */
    public static String generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(DEFAULT_KEY_SIZE, new SecureRandom());
        byte[] bytes = kg.generateKey().getEncoded();
        return ByteUtils.toHexString(bytes);
    }

    /**
     * sm4加密：密文长度不固定，会随着被加密字符串长度变化而变化
     *
     * @param hexKey   16进制秘钥（忽略大小写）
     * @param paramStr 待加密字符串
     * @return 返回十六进制的加密字符串
     * @throws Exception
     */
    public static String encrypt(String hexKey, String paramStr) throws Exception {
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = paramStr.getBytes(ENCODING);
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, keyData);
        byte[] cipherArray = cipher.doFinal(srcData);
        return ByteUtils.toHexString(cipherArray);
    }

    /**
     * sm4 解密：解密模式采用ECB
     *
     * @param hexKey     16进制秘钥
     * @param cipherText 16进制的加密字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String hexKey, String cipherText) throws Exception {
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] srcData = decryptEcbPadding(keyData, cipherData);
        return new String(srcData, ENCODING);
    }

    /**
     * 解密模式Ecb
     *
     * @param key
     * @param cipherText
     * @return
     * @throws Exception
     */
    private static byte[] decryptEcbPadding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    /**
     * 加密数据校验
     *
     * @param hexKey     16进制秘钥(忽略大小写)
     * @param cipherText 16进制加密后的字符串
     * @param paramStr   加密签的字符串
     * @return 加密前后是否一致
     * @throws Exception
     */
    public static boolean verify(String hexKey, String cipherText, String paramStr) throws Exception {
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        byte[] decryptData = decryptEcbPadding(keyData, cipherData);
        byte[] srcData = paramStr.getBytes(ENCODING);
        return Arrays.equals(decryptData, srcData);
    }

    public static void main(String[] args) {
        try {
            //获取key
            String k = generateKey();
            System.out.println(k.toUpperCase());

            String json = "{\"name\":\"Marydon\",\"website\":\"http://www.baidu.com\"}";
            String key = "86C63180C2806ED1F47B859DE501215B";

            //加密
            String encrypt = Sm4Util.encrypt(key, json);
            System.out.println(encrypt);

            //验证
            System.out.println(Sm4Util.verify(key, encrypt, json));

            System.out.println(Sm4Util.decrypt(key, encrypt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

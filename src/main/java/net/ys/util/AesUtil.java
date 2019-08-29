package net.ys.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用AES算法完成对数据的加密和解密
 */
public class AesUtil {

    private static final String IV_STRING = "6868688xy8686866";//偏移量
    private static final String KEY = "8686866xy6868688"; //密码
    private static final String ALGORITHM = "AES";//算法
    private static final String ENCRYPTION_MODE = "CBC";//加密模式，数据块128
    private static final String FILL_TYPE = "PKCS5Padding";//填充
    private static final String ENCODING = "UTF-8";

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encryptAES(String content) {
        try {
            byte[] byteContent = content.getBytes(ENCODING);
            byte[] enCodeFormat = KEY.getBytes(ENCODING);
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);
            byte[] initParam = IV_STRING.getBytes(ENCODING);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + ENCRYPTION_MODE + "/" + FILL_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(byteContent);
            return Base64Util.encode(encryptedBytes);
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decryptAES(String content) {
        try {
            byte[] encryptedBytes = Base64Util.decode(content.getBytes(ENCODING));
            byte[] enCodeFormat = KEY.getBytes(ENCODING);
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, ALGORITHM);
            byte[] initParam = IV_STRING.getBytes(ENCODING);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + ENCRYPTION_MODE + "/" + FILL_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, ENCODING);
        } catch (Exception e) {
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        String str = encryptAES("1567629198000");
        System.out.println(str);
        System.out.println(decryptAES(str));
    }
}
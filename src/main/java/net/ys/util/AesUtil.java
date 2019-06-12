package net.ys.util;

import net.ys.constant.X;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用AES算法完成对数据的加密和解密
 */
public class AesUtil {

    private static final String IV_STRING = "6868688xy8686866";
    private static final String KEY = "8686866xy6868688";

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encryptAES(String content) {
        try {
            byte[] byteContent = content.getBytes(X.Code.U);
            byte[] enCodeFormat = KEY.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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
            byte[] encryptedBytes = Base64Util.decode(content.getBytes());
            byte[] enCodeFormat = KEY.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, X.Code.U);
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
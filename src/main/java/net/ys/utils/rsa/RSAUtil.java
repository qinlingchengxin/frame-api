package net.ys.utils.rsa;

import net.ys.constant.X;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

    public static final String ENCRYPTION_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA最大加密明文大小《规定》
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小《规定》
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成密钥
     */
    public static MyPair<PublicKey, PrivateKey> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new MyPair(keyPair.getPublic(), keyPair.getPrivate());
    }

    /**
     * 取得公钥
     */
    public static String getPublicKey(MyPair<PublicKey, PrivateKey> pair) throws Exception {
        return Base64Util.encryptBASE64(pair.getL().getEncoded()).replaceAll("\\s", "");
    }

    /**
     * 取得私钥
     */
    public static String getPrivateKey(MyPair<PublicKey, PrivateKey> pair) throws Exception {
        return Base64Util.encryptBASE64(pair.getR().getEncoded()).replaceAll("\\s", "");
    }

    /**
     * 加密
     */
    public static String encrypt(String data, String keyString, boolean isPublic) throws Exception {
        MyPair<Key, KeyFactory> pair = generateKeyAndFactory(keyString, isPublic);
        KeyFactory keyFactory = pair.getR();
        Key key = pair.getL();
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] dataBytes = data.getBytes(X.Code.U);
        int dataLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;

        while (dataLen - offSet > 0) {//对数据分段加密
            if (dataLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offSet, dataLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64Util.encryptBASE64(encryptedData).replaceAll("\\s", "");
    }

    /**
     * 解密
     */
    public static String decrypt(String data, String keyString, boolean isPublic) throws Exception {

        MyPair<Key, KeyFactory> pair = generateKeyAndFactory(keyString, isPublic);
        KeyFactory keyFactory = pair.getR();
        Key key = pair.getL();
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] dataBytes = Base64Util.decryptBASE64(data);

        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {//对数据分段解密
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, X.Code.U);
    }

    /**
     * 生成钥匙
     */
    public static MyPair<Key, KeyFactory> generateKeyAndFactory(String keyString, boolean isPublic) throws Exception {
        byte[] keyBytes = Base64Util.decryptBASE64(keyString);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        Key key;
        if (isPublic) {
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(keyBytes);
            key = keyFactory.generatePublic(encodedKeySpec);
        } else {
            PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            key = keyFactory.generatePrivate(encodedKeySpec);
        }
        return new MyPair(key, keyFactory);
    }

    /**
     * 对信息生成数字签名（用私钥）
     */
    public static String sign(byte[] data, String keyString) throws Exception {
        MyPair<Key, KeyFactory> pair = generateKeyAndFactory(keyString, false);
        Key key = pair.getL();
        PrivateKey privateKey = (PrivateKey) key;
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return Base64Util.encryptBASE64(signature.sign());
    }

    /**
     * 校验数字签名（用公钥）
     */
    public static boolean verify(byte[] data, String keyString, String sign) throws Exception {
        MyPair<Key, KeyFactory> pair = generateKeyAndFactory(keyString, true);
        Key key = pair.getL();
        PublicKey publicKey = (PublicKey) key;
        // 取公钥匙对象
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64Util.decryptBASE64(sign));
    }

    public static void main(String[] args) throws Exception {
        MyPair<PublicKey, PrivateKey> pair = RSAUtil.genKeyPair();//公钥私钥
        String publicKey = RSAUtil.getPublicKey(pair);
        System.out.println(publicKey);
        String privateKey = RSAUtil.getPrivateKey(pair);
        System.out.println(privateKey);
        String str = "中国";

        String encryptStr = encrypt(str, publicKey, true);
        System.out.println(encryptStr);

        String decryptStr = decrypt(encryptStr, privateKey, false);
        System.out.println(decryptStr);
    }
}
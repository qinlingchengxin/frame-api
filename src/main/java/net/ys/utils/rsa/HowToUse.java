package net.ys.utils.rsa;

import java.security.PrivateKey;
import java.security.PublicKey;


public class HowToUse {

    private String publicKey = null;
    private String privateKey = null;

    public void setUp() throws Exception {
        MyPair<PublicKey, PrivateKey> pair = RSAUtil.genKeyPair();
        publicKey = RSAUtil.getPublicKey(pair);
        privateKey = RSAUtil.getPrivateKey(pair);

        System.out.println("公钥 -> " + publicKey);
        System.out.println("私钥 -> " + privateKey);
    }

    public void test() throws Exception {
        System.out.println("公钥加密，私钥解密");
        String sourceString = "hi, RSA";

        String encodedData = RSAUtil.encrypt(sourceString, publicKey, true);
        String decodedData = RSAUtil.decrypt(encodedData, privateKey, false);

        System.out.println("加密前: " + sourceString + "，解密后: " + decodedData);
    }

    public void test2() throws Exception {
        System.out.println("私钥签名，公钥验证签名");
        String sourceString = "hello, RSA sign";
        byte[] data = sourceString.getBytes();

        // 产生签名
        String sign = RSAUtil.sign(data, privateKey);
        System.out.println("签名 -> " + sign);

        // 验证签名
        boolean status = RSAUtil.verify(data, publicKey, sign);
        System.out.println("状态 -> " + status);
    }

    public void test3() throws Exception {
        System.out.println("私钥加密，公钥解密");
        String sourceString = "hello, reRSA";
        String encodedData = RSAUtil.encrypt(sourceString, privateKey, false);
        String decodedData = RSAUtil.decrypt(encodedData, publicKey, true);
        System.out.println("加密前: " + sourceString + "，解密后: " + decodedData);
    }

    public static void main(String[] args) {

    }
}
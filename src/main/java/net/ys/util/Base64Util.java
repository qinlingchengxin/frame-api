package net.ys.util;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

    public static byte[] decode(byte[] bytes) {
        return Base64.decodeBase64(bytes);
    }

    public static String encode(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }
}
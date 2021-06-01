package com.megumi.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 2021/2/21
 *
 * @author miyabi
 * @since 1.0
 */
public class Encryption {

    /**
     * @param str 要加密的字符串
     * @return SHA256加密后的字符串
     */
    public static String sha256(String str) throws NoSuchAlgorithmException {
        var messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        return byteToHex(messageDigest.digest());
    }

    /**
     * @param bytes 要转为十六进制字符串的字节数组
     * @return 十六进制字符串
     */
    public static String byteToHex(byte[] bytes) {
        var sb = new StringBuilder();
        for (var aByte : bytes) {
            var temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static String encodeBase64Str(byte[] bytes) {
        var encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    public static byte[] decodeBase64Str(String base64Str) {
        var decoder = Base64.getDecoder();
        return decoder.decode(base64Str);
    }

}

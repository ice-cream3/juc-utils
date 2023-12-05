package com.example.demo;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @ClassName: TestMain
 * @Description:
 * @Author: ice
 * @Date: 2023/6/28 12:05
 */
public class GoogleAuthenticator {
    //base64随机字符
    public static String BASE64 = "";
    //谷歌验证码前缀
    public static String PREFIX = "123";
    //谷歌验证码备注
    public static String REMARK = "123";
    //生成的二维码的路径及名称
    public static String DESTPATH = "D:/code.jpg";

    public static void main(String[] args) throws Exception {
        // 1.生成随机密钥
        String code = generateSecretKey();
        // only
        code = "D4BKJ2ZDZTDJPPPY";
        // many
//        code = "62X2MEBXSNOSLRAW";
        System.out.println("密钥:" + code);
        // 2.生成验证码
        Integer verifyCode = getVerifyCode(code);
        System.out.println("验证码:" + verifyCode);
        // 3.将生成的密钥转换为二维码
        // QRCode(code);
    }

    /**
     * 生成随机密钥
     */
    public static String generateSecretKey() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(Base64.decodeBase64(BASE64));
        byte[] buffer = sr.generateSeed(10);
        Base32 codec = new Base32();
        byte[] bEncodedKey = codec.encode(buffer);
        return new String(bEncodedKey);
    }

    /**
     * 生成验证码
     */
    public static Integer getVerifyCode(String code) {
        byte[] key = new Base32().decode(code);
        long t = (System.currentTimeMillis() / 1000L) / 30L;
        int verifyCode = 0;
        try {
            verifyCode = GoogleAuthenticator.generateCode(key, t);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return verifyCode;
    }

    /**
     * 二维码图片
     */
    public static void QRCode(String code) throws Exception {
        //user 为大标题括号内备注 code是验证吗 issuer 是前缀
        String admin = getQRBarcode(PREFIX, code, REMARK);
        //生成二维码
        QRCodeUtil.encode(admin, DESTPATH);
        //解析二维码
        String str = QRCodeUtil.decode(new File(DESTPATH));
        //打印出解析出的内容
        System.out.println("str:" + str);
    }

    public static String getQRBarcode(String user, String secret, String issuer) {
        String format = "otpauth://totp/%s?secret=%s&issuer=%s";
        return String.format(format, user, secret, issuer);
    }

    public static int generateCode(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

    /**
     * 最多可偏移的时间
     * default 3 - max 17
     */
    int window_size = 3;

    public boolean checkCode(String secret, long code, long timeMsec) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        long t = (timeMsec / 1000L) / 30L;
        for (int i = -window_size; i <= window_size; ++i) {
            long hash;
            try {
                hash = generateCode(decodedKey, t + i);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            if (hash == code) {
                return true;
            }
        }
        return false;
    }

}

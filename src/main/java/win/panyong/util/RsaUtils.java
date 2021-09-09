package win.panyong.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RsaUtils {
    // rsa密钥长度
    private static final int DEFAULT_RSA_KEY_SIZE = 2048;

    // 密钥算法
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * @param @return 设定文件
     * @return Map<String, String> 返回类型 @throws
     * @Title: generateRsaKey
     * @Description: 生成密钥对
     */
    public static Map<String, String> generateRsaKey() {
        Map<String, String> result = new HashMap<>(2);
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            // 初始化密钥对生成器，密钥大小为1024 2048位
            keyPairGen.initialize(DEFAULT_RSA_KEY_SIZE, new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 得到公钥字符串
            result.put("publicKey", new String(Base64.encodeBase64(keyPair.getPublic().getEncoded())));
            // 得到私钥字符串
            result.put("privateKey", new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded())));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String decrypt(String str, String rsaKey) throws Exception {
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        // base64编码的私钥
        byte[] decoded = Base64.decodeBase64(rsaKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    /**
     * @Title: encrypt @Description: RSA 加密 @param @param content
     * 待加密的内容 @param @param rsaKey @param @return @param @throws Exception
     * 设定文件 @return byte[] 返回类型 @throws
     */
    public static byte[] encrypt(byte[] content, String rsaKey) throws Exception {
        byte[] buffer = Base64.decodeBase64(rsaKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    public static void main(String[] args) throws Exception {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqlcjF8Jf7YDWLm4nWHIin0hXYfVrAsXmV9UIkSVfpgxFeVBlzYr1iwJ7TRgsdIoWnEwPC6386mSdNimSIomqsK/Lz5Z2mOYDubNKtYb/Yx6U24OO3asSyZ/pxQDNotvl/7CLfoGN/OK6J3BvJb1BTR+W0Kfrhyx3QG2aFLdxKZjZrSIHdhQPEZJONkIkVE+Xu/hNVfzABT/t3kbImeuIfzAxEJPyzYCr7IxT0QiSn/mh39d8oupRonWi97Gc47gyhH7U9YpdHf/r+MJ/1UIQOky2cGqD34YL5hAQly1HInQ9kZXna77Ovbf3oBmZPuUJWiX6e523AKiveWpZNhkBlQIDAQAB";
        String rsaText = "hello world";
        String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCqVyMXwl/tgNYubidYciKfSFdh9WsCxeZX1QiRJV+mDEV5UGXNivWLAntNGCx0ihacTA8LrfzqZJ02KZIiiaqwr8vPlnaY5gO5s0q1hv9jHpTbg47dqxLJn+nFAM2i2+X/sIt+gY384roncG8lvUFNH5bQp+uHLHdAbZoUt3EpmNmtIgd2FA8Rkk42QiRUT5e7+E1V/MAFP+3eRsiZ64h/MDEQk/LNgKvsjFPRCJKf+aHf13yi6lGidaL3sZzjuDKEftT1il0d/+v4wn/VQhA6TLZwaoPfhgvmEBCXLUcidD2Rledrvs69t/egGZk+5QlaJfp7nbcAqK95alk2GQGVAgMBAAECggEAbSq1Gdf/hIXKPi1oV92l5LEHF4eiUj+kdOvZtrPeDdvVdn8ZD1acML+hZ/IzI2kQPpDcH58c9NcQjhKsTiguVVgE5YBHd4wKHSwTmCzxZYPG4Lv6hoPJ4Z/zwMbYAySszWZGsAe1iLmBlh8PjH8y54t0KztCzfmoX4kec2JxSrdWORWeXdPRm+KZFnYtd8RtPtlunteoGXT8vtpKl9qE9/iZN1Qt43dQWRMwHuWb8eXKbKbeJ9WCxIbfCNkzfNxzddUfOdjCE2yjYOTYL+I8FxeGCm3KeGzM7w9jYFrW9ha5uDoBrG6aA4ICeser905Aa6/13+21WuX6rNzkdWlNQQKBgQDr770ejbpPGOSvJjInN6oPMM7rFz2ewq28+tCFDEaRXOO+uGmeQCAMSpJMR59fwR2FNhCBKve+hWP/BSKtCrqu3zC/hZYSFKwOe8Pw/DzHorwmAVPZr/GJFJXTpYnEDSlEX7yAs4RuJuPIWzg6BA7PosZk2eKie1dcVDUE7KoCBQKBgQC402RwDrfTMnbrBPlPLsMKJeGnYtu8C1MZdDCR4vTsAFhyXoUm5WU7ivblNvuH2fFTt6wcNpRobFy3Yu6guIKJpc0fl4kwy8NHUflNtH18LD6iFesZAWoLV9X0UPuiCmQB017wcqHxQWsM6xxkb2fZ4J9ljel+GKVu/qpjv41GUQKBgG9AmcXpgLvBejwY37dcaSVGl5uFVvogxHoDLwY91py+12lcXflQNYx2MWwkrcMiNcBV0QyHbVD6Zz+edU5xa7v/5GW7IRufgc5GkAt3dVWRp/Sn1ZklfNhty4SEX2UB676hAisR+1VRo0EbunSo6y7/i1uM3nBNhtEzp9iNnFDdAoGAfGdNI1/Eu5MsF+SxKmr/PMVirZgM7vVwaaGbT5bD5FYXMmQMm7GsAff3VzE6/KXlmcP3RMY3/lFx1r3wgJ2wv5WqC6mj3gRI3KiAdZ0XGY1uWwsFwz8AccIdcOVRejAkQFZVgOKnvguaUoeuBKw36p8WqvRqRUWxlJQ1fA5iuQECgYBuRaAIp2gTO2nka5s/aEXPULjq+p6V9n9asQ5Lies5bFIlHcgy4f0w9LgfnePa7hCvDdRPSztEu1ONWKYsMLexrZNuOcPeIK206zBPAZGva1sZ/74k+wvMfTPy9Mf8+xRZplUjZn1fsGq+lX1hyIDpZc0d8YKZHESa/rjtd1CDsg==";
        // 公钥加密
        byte[] rsaByte = encrypt(rsaText.getBytes(), publicKey);
        String rsaStr = Base64.encodeBase64String(rsaByte);
        // 私钥解密
        System.out.println(decrypt(rsaStr, privateKey));
    }
}

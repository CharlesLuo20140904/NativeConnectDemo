package com.yzt400.yzt.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {
    public static final String RSA_ECB_PKCS1Padding = "RSA/ECB/PKCS1Padding";

    public static byte[] loadPemKey(byte[] pem, boolean delSplit) throws Exception {
        if (!delSplit) {
            return pem;
        } else {
            return loadPemKey(new ByteArrayInputStream(pem), delSplit);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static byte[] loadPemKey(InputStream in, boolean delSplit) throws Exception {
        if (!delSplit) {
            try {
                return IoUtil.read(in).toByteArray();
            } finally {
                in.close();
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = null;
                StringBuilder key = new StringBuilder();
                String regex = "^---.+---$";
                int index = 0;
                boolean split0 = false;
                boolean split1 = false;
                while ((line = reader.readLine()) != null) {
                    if (line.matches(regex) && index++ == 0) {
                        if (key.toString().trim().length() > 0) {
                            throw new RuntimeException("密钥文件读取失败");
                        }
                        split0 = true;
                    } else if (line.matches(regex) && index++ > 1) {
                        if (key.toString().trim().length() == 0) {
                            throw new RuntimeException("密钥文件读取失败");
                        }
                        split1 = true;
                        break;
                    } else {
                        index++;
                        key.append(line);
                        key.append("\n");
                    }
                }
                if (split0 != split1) {
                    throw new RuntimeException("密钥文件读取失败");
                }
                return key.toString().trim().getBytes();
            } finally {
                IoUtil.close(in);
            }
        }
    }

    public static void makekeyfile(String pubkeyfile, String privatekeyfile, int keySize)
            throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为1024位
        keyPairGen.initialize(keySize);
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        FileOutputStream out = new FileOutputStream(privatekeyfile);
        out.write(Base64.encode(privateKey.getEncoded(), Base64.NO_WRAP));
        out.flush();
        out.close();
        out = new FileOutputStream(pubkeyfile);
        out.write(Base64.encode(publicKey.getEncoded(), Base64.NO_WRAP));
        out.flush();
        out.close();
        System.out.println("ok!");
    }

    public static byte[] encryptByPrivateKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptByPrivateKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] encryptByPublicKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptByPublicKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // BC驱动处理
    public static byte[] bcEncryptByPrivateKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] bcDecryptByPrivateKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] bcEncryptByPublicKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] bcDecryptByPublicKey(byte[] data, byte[] key, String transformation) throws Exception {
        key = Base64.decode(key, Base64.NO_WRAP);
        
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
}


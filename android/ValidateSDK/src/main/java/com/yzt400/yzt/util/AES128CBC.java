package com.yzt400.yzt.util;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES128CBC {

	private static final String AESType = "AES/CBC/NoPadding";
	private static final String CharType = "UTF-8";
	private static final String SecretType = "AES";

	public static String[] createKeyAndIV() {
		final StringBuilder str = new StringBuilder();
		int v = 126 - 33;
		while (str.length() < 32) {
			int i = (int) (Math.random() * v) + 33;
			str.append((char) i);
		}
		return new String[] { str.substring(0, 16), str.substring(16) };
	}

	public static Cipher getCipher(String key, String iv, int opmode) throws Exception {
		if (key == null || key.length() != 16) {
			throw new InvalidKeyException("key is wrong");
		}
		if (iv == null || iv.length() != 16) {
			throw new InvalidAlgorithmParameterException("iv  is wrong");
		}
		SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(CharType), SecretType);
		Cipher cipher = Cipher.getInstance(AESType);
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(CharType));
		cipher.init(opmode, sKeySpec, ivSpec);
		return cipher;
	}

	public static byte[] getSrcByte(String src, int blockSize) throws Exception {
		byte[] dataBytes = src.getBytes(CharType);
		int plaintextLength = dataBytes.length;
		if (plaintextLength % blockSize != 0) {
			plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
		}
		byte[] plaintext = new byte[plaintextLength];
		System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
		return plaintext;
	}

	public static String encrypt(String src, String key, String iv) throws Exception {
		Cipher cipher = getCipher(key, iv, Cipher.ENCRYPT_MODE);
		// return TypeConversion.bytes2HexString(cipher.doFinal(getSrcByte(src,
		// cipher.getBlockSize())));
		return  Base64.encodeToString(cipher.doFinal(getSrcByte(src, cipher.getBlockSize())),Base64.NO_WRAP);
	}

	public static String decrypt(String src, String key, String iv) throws Exception {
		Cipher cipher = getCipher(key, iv, Cipher.DECRYPT_MODE);
		// return new
		// String(cipher.doFinal(TypeConversion.hexString2Bytes(src)));
		byte[] data = cipher.doFinal(Base64.decode(src,Base64.NO_WRAP));
		int i = data.length - 1;
		for (; i >= 0; i--) {
			if (data[i] != 0)
				break;
		}
		return new String(data, 0, i + 1, CharType);
	}

	public static void main(String[] args) throws Exception {
		String key = "35150511B865466C";
		String iv = "B16FCC9B7C477F78";
		String str = "中国";
		String enStr = AES128CBC.encrypt(str, key, iv);
		System.out.println(enStr);
		String s = AES128CBC.decrypt(enStr, key, iv);
		System.out.println(s);
	}
}
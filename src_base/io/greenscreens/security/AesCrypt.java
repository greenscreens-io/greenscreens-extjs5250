/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.security;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AEC encryption & Decryption utility
 */
public class AesCrypt implements ICrypt {

	private static String SecretKey = "0123456789abcdef";// Dummy secretKey (CHANGE IT!)
	private static String iv = "fedcba9876543210";// Dummy iv (CHANGE IT!)
	
	private static IvParameterSpec ivspec;
	private static SecretKeySpec keyspec;
	private static Cipher cipher;
	
    static {
		try {
			setSecretKey(SecretKey);
			setIv(iv);
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
    }

    /**
     * Set key used to encrypt data
     * @param secretKey
     */
	public static void setSecretKey(String secretKey) {
		AesCrypt.SecretKey = secretKey;
		AesCrypt.keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");
	}
	
	/**
	 * Set Initialization vector to encrypt data to prevent 
	 * same hash for same passwords
	 * @param iv
	 */
	public static void setIv(String iv) {
		AesCrypt.iv = iv;
		AesCrypt.ivspec = new IvParameterSpec(iv.getBytes());
	}

	/**
	 * Encrypt string and return raw byte's 
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptData(String text) throws Exception {
		
		if (text == null || text.length() == 0) {
			throw new Exception("Empty string");
		}

		byte[] encrypted = null;

		try {
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			encrypted = cipher.doFinal(padString(text).getBytes());
		} catch (Exception e) {
			throw new Exception("[encrypt] " + e.getMessage());
		}

		return encrypted;
	}

	/**
	 * Decrypt hex encoded data to byte array
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptData(String code) throws Exception {
		
		if (code == null || code.length() == 0) {
			throw new Exception("Empty string");
		}

		byte[] decrypted = null;

		try {
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			decrypted = cipher.doFinal(hexToBytes(code));
		} catch (Exception e) {
			throw new Exception("[decrypt] " + e.getMessage());
		}
		return decrypted;
	}

	/**
	 * Converts raw bytes to string hex 
	 * @param data
	 * @return
	 */
	public static String bytesToHex(byte[] data) {
		
		if (data == null) {
			return null;
		}

		int len = data.length;
		String str = "";
		for (int i = 0; i < len; i++) {
			if ((data[i] & 0xFF) < 16) {
				str = str + "0" + java.lang.Integer.toHexString(data[i] & 0xFF);
			} else {
				str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
			}
		}
		return str;
	}

	/**
	 * Convert string hex to raw byte's
	 * @param str
	 * @return
	 */
	public static byte[] hexToBytes(String str) {
		
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}
	}

	/**
	 * Encrypts string to hex string 
	 */
	public String encrypt(String text) throws Exception {
		return AesCrypt.bytesToHex(AesCrypt.encryptData(text));		
	}
	
	/**
	 * Decrypts hex string to string value
	 */
	public String decrypt(String text) throws Exception {
		return new String(AesCrypt.decryptData(text));
	}	
	
	/**
	 * Blank padding for AES algorithm
	 * @param source
	 * @return
	 */
	private static String padString(String source) {
		char paddingChar = ' ';
		int size = 16;
		int x = source.length() % size;
		int padLength = size - x;

		for (int i = 0; i < padLength; i++) {
			source += paddingChar;
		}

		return source;
	}
	
}

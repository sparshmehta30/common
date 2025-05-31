package com.common.utils;

import java.io.UnsupportedEncodingException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Encryption {
	private static String myKey = "Tg2Nn7wUZOQ6Xc";
	private static final String AES_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static final String DEFAULT_ENCRYPTION_ALGORITHM = "MD5";
	private static final Logger LOGGER = Logger.getLogger(Encryption.class);

	public static SecretKeySpec getKey() {
		MessageDigest sha = null;
		try {
			byte[] key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			return new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encrypt(String strToEncrypt) {
		try {
			SecretKeySpec secretKey = getKey();
			Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return DatatypeConverter.printHexBinary(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			LOGGER.info("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		try {
			SecretKeySpec secretKey = getKey();
			Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(DatatypeConverter.parseHexBinary(strToDecrypt)));
		} catch (Exception e) {
			LOGGER.info("Error while decrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * Encrypt.
	 *
	 * @param input the input
	 * @return the string
	 */
	public static String encryptMD5(String input) {
		return encrypt(input, DEFAULT_ENCRYPTION_ALGORITHM);
	}

	/**
	 * Encrypt.
	 *
	 * @param input         the input
	 * @param algorithmName the algorithm name
	 * @return the string
	 */
	public static String encrypt(String input, String algorithmName) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithmName);
			md.update(input.getBytes());
			byte[] mdbytes = md.digest();
			StringBuffer output = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				output.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return output.toString();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			// LOGGER.error(ex.getMessage(), ex);
		}
		return "";
	}

	public static String base64Encrypt(String stringForEncrypt) {
		try {
			byte[] bytesEncoded = org.apache.commons.codec.binary.Base64.encodeBase64(stringForEncrypt.getBytes());
			return new String(bytesEncoded);
		} catch (Exception e) {
			return stringForEncrypt;
		}
	}

	public static String base64Decrypt(String encryptedString) {
		try {
			byte[] valueDecoded = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedString);
			return new String(valueDecoded);
		} catch (Exception e) {
			return encryptedString;
		}
	}
}

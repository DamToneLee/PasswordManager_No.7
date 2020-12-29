package test;

import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptAES {

	public static void main(String... arg) {
		CryptAES main = new CryptAES("thisIsAMasterPassword", "thisIsASalt", 256, 9981);
		CryptAES main2 = new CryptAES("thisIsAMasterPassword", "thisIsASalt", 256, 9981);
		try {
			String text = "hello world";
			System.out.println("text length (bits): " + text.length() * Character.BYTES * 8);
			System.out.println("key length (bits): " + main.key.getEncoded().length * 8);
			System.out.println("key text: "+Arrays.toString(main.key.getEncoded()));
			System.out.println("key2 text: "+Arrays.toString(main2.key.getEncoded()));
			
			byte[] encryptText = main.encrypt(text);
			String decryptText = main.decrypt(encryptText);
			System.out.println("encrypt text: "+Arrays.toString(encryptText));
			System.out.println("decrypt text: "+decryptText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Cipher cipher;
	SecretKey key;
	byte[] iv;

	public CryptAES(String password, String salt, int keybits, int iterationCount) {
		key = genKey(password, salt, keybits, iterationCount);
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public byte[] encrypt(String data) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		AlgorithmParameters params = cipher.getParameters();
		iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encrypt = cipher.doFinal(data.getBytes());
		return encrypt;
	}

	public String decrypt(byte[] encrypt) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		byte[] text = cipher.doFinal(encrypt);
		return new String(text);
	}

	private SecretKeySpec genKey(String masterPassword, String salt, int keybits, int iterationCount) {
		SecretKeySpec key = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			SecretKey genKey = factory.generateSecret(
					new PBEKeySpec(masterPassword.toCharArray(), salt.getBytes(), iterationCount, keybits));
			key = new SecretKeySpec(genKey.getEncoded(), "AES");
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return key;
	}
}
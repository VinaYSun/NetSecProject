package utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class CryptoUtils {
	
	/**
	 * generate nonce
	 * @return 1024 bits nonce
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] generateNonce() throws UnsupportedEncodingException { 
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[1024/8];
	    random.nextBytes(bytes);
	    System.out.println("Nounce is  "+ bytes + "with length " + bytes.length);
	    return bytes;
	}
	
	public static byte[] generateDHKey(){
		
		return null;
	}
	
	public static byte[] generateSessionKey(){
		
		return null;
	}
	
	public static byte[] getSignature(){
		
		return null;
	}
	
	public static PrivateKey getPrivateKey(String privateKeyFileName) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		byte[] privateKey = new byte[Integer.MAX_VALUE];
		privateKey = FileUtils.toByteArray(privateKeyFileName);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
		PrivateKey key = rsaKeyFactory.generatePrivate(privateKeySpec);
		return key;
	}
	
	public static PublicKey getPublicKey(String publicKeyFileName) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		byte[] publicKey = new byte[Integer.MAX_VALUE];
		publicKey = FileUtils.toByteArray(publicKeyFileName);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
		PublicKey key = rsaKeyFactory.generatePublic(publicKeySpec);
		return key;
	}
	
	public static byte[] getHash(){
		
		return null;
	}
	
	public static byte[] getSaltHash(){
		
		return null;
	}
	
	public static byte[] encrypt(String algorithm, byte[] plaintext){
		
		return null;
	}
	
	public static byte[] decrypt(String algorithm, byte[] cyphertext){
		
		return null;
	}
	
	public static byte[] appendAndEncrypt(String algorithm, byte[] plaintext1, byte[] plaintext2, byte[] plaintext3){
		
		return null;
	}

}

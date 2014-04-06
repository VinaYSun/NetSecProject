package utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CryptoUtils {
	
	/**
	 * Get 1024 bits nonce
	 * @return byte
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] generateNonce() throws UnsupportedEncodingException, NoSuchAlgorithmException { 
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] bytes = new byte[1024/8];
	    random.nextBytes(bytes);
	    System.out.println("Nounce is  "+ bytes + "with length " + bytes.length);
	    return bytes;
	}
	
	/**
	 * Generate 512-bits DH key pair
	 */
	public static Map<String, Key> generateDHKey() throws Exception{  
		KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("DH");  
        keyPairGenerator.initialize(512);  
        KeyPair keyPair=keyPairGenerator.generateKeyPair();  
        DHPublicKey publicKey=(DHPublicKey) keyPair.getPublic();  
        DHPrivateKey privateKey=(DHPrivateKey) keyPair.getPrivate();  
        Map<String,Key> keyMap = new HashMap<String,Key>();  
        keyMap.put("public_key", publicKey);  
        keyMap.put("private_key", privateKey);  
        return keyMap;  
    }  
	
	/**
	 * Generate 512-bits DH key pair on the basis of input key 
	 * @param public key from the other side
	 * @return Map<keyname, key>
	 * @throws Exception
	 */
	public static Map<String,Key> generateDHKey(byte[] key) throws Exception{  
	    X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(key);  
	    KeyFactory keyFactory=KeyFactory.getInstance("DH");  
	    PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);  
	    DHParameterSpec dhParamSpec=((DHPublicKey)pubKey).getParams();  
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(keyFactory.getAlgorithm());  
        keyPairGenerator.initialize(dhParamSpec);  
        KeyPair keyPair=keyPairGenerator.genKeyPair();  
        DHPublicKey publicKey=(DHPublicKey)keyPair.getPublic();  
        DHPrivateKey privateKey=(DHPrivateKey)keyPair.getPrivate();  
        Map<String,Key> keyMap=new HashMap<String,Key>();  
        keyMap.put("public_key", publicKey);  
        keyMap.put("private_key", privateKey);  
        return keyMap;  
	}
	
	/**
	 * Generate Session Key
	 * @return session key
	 * @throws IllegalStateException 
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	public static byte[] generateSessionKey(byte[] publicKey, byte[] privateKey) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		KeyFactory keyFactory=KeyFactory.getInstance("DH");  
        X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(publicKey);  
        PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);  
        PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(privateKey);  
        PrivateKey priKey=keyFactory.generatePrivate(pkcs8KeySpec);  
        KeyAgreement keyAgree=KeyAgreement.getInstance(keyFactory.getAlgorithm());  
        keyAgree.init(priKey);  
        keyAgree.doPhase(pubKey, true);  
        SecretKey secretKey=keyAgree.generateSecret("AES");  
        return secretKey.getEncoded();  
	}
	
	/**
	 * AES Encrypt
	 * @param data
	 * @param AES key
	 * @return encrypted data
	 * @throws Exception
	 */
    public static byte[] encryptByAES(byte[] data, byte[] key) throws Exception{  
        SecretKey secretKey=new SecretKeySpec(key, "AES");  
        Cipher cipher=Cipher.getInstance(secretKey.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);  
        return cipher.doFinal(data);  
    }  
    
	/**
	 * AES Encrypt
	 * @param data
	 * @param AES key
	 * @return encrypted data
	 * @throws Exception
	 */
    public static byte[] encryptByAES(byte[] data, Key key) throws Exception{  
        Cipher cipher=Cipher.getInstance("AES");  
        cipher.init(Cipher.ENCRYPT_MODE, key);  
        return cipher.doFinal(data);  
    }  
    
	/**
	 * AES Decrypt
	 * @param data
	 * @param AES key
	 * @return derypted data
	 * @throws Exception
	 */
    public static byte[] decryptByAES(byte[] data, byte[] key) throws Exception{  
        SecretKey secretKey=new SecretKeySpec(key,"AES");  
        Cipher cipher=Cipher.getInstance(secretKey.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, secretKey);  
        return cipher.doFinal(data);  
    }  
	
	/**
	 * AES Encrypt
	 * @param data
	 * @param AES key
	 * @return encrypted data
	 * @throws Exception
	 */
    public static byte[] decryptByAES(byte[] data, Key key) throws Exception{  
        Cipher cipher=Cipher.getInstance("AES");  
        cipher.init(Cipher.DECRYPT_MODE, key);  
        return cipher.doFinal(data);  
    }  
    
	public static byte[] getSignature(){
		
		return null;
	}
	
	public static PrivateKey getPrivateKey(String privateKeyFileName) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		byte[] privateKey = FileUtils.toByteArray(privateKeyFileName);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
		PrivateKey key = rsaKeyFactory.generatePrivate(privateKeySpec);
		System.out.println("private key is generated");
		return key;
	}
	
	public static PublicKey getPublicKey(String publicKeyFileName) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		byte[] publicKey = FileUtils.toByteArray(publicKeyFileName);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
		PublicKey key = rsaKeyFactory.generatePublic(publicKeySpec);
		System.out.println("public key is generated");
		return key;
	}
	
	public static Key generateKeyFromPassword(byte[] key) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		key = md.digest(key);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		return secretKeySpec;
	}
	 
	public static byte[] encryptByRSAPublicKey(byte[] data, Key publicKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
	}
	
	public static byte[] decryptByRSAPrivateKey(byte[] data, Key privateKey)throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
	}
	
	public static byte[] encryptByRSAPrivateKey(byte[] data, Key privateKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
	}
	
	public static byte[] decryptByRSAPublicKey(byte[] data, Key publicKey) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
	}
	
	public static byte[] encryptByRSAPublicKey(byte[] data, byte[] publicKey) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
		PublicKey key = rsaKeyFactory.generatePublic(publicKeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
	}
	
	public static byte[] decryptByRSAPrivateKey(byte[] data, byte[] privateKey)throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec	 privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
		PrivateKey key = rsaKeyFactory.generatePrivate(privateKeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
	}
	
	public static byte[] encryptByRSAPrivateKey(byte[] data, byte[] privateKey) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
		PrivateKey key = rsaKeyFactory.generatePrivate(privateKeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
	}
	
	public static byte[] decryptByRSAPublicKey(byte[] data, byte[] publicKey) throws Exception{
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
		PublicKey key = rsaKeyFactory.generatePublic(publicKeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
	}
	/**
	 * Hash with salt using SHA-256
	 * @param string
	 * @param salt
	 * @return hash String
	 * @throws NoSuchAlgorithmException 
	 */
	public static byte[] getMD5(byte[] data) throws NoSuchAlgorithmException{
		  MessageDigest md = MessageDigest.getInstance("SHA-256");
          md.update(data);
          return md.digest();
	}

	/**
	 * Hash with salt using SHA-256
	 * @param string
	 * @param salt
	 * @return hash String
	 */
	public static String getSaltHash(String string, String salt)
	{
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(string.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

	/**
	 * Get 128-bits salt
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
    public static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        sr.nextBytes(salt);
        return salt.toString();
    }

    /**
     * Convert hex to string
     * @param byte
     * @return String
     */
    public static String bytesToHexString(byte[] b){  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (b == null || b.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < b.length; i++) {  
            int v = b[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    }  
    
    /**
     * Convert string to hexByte 
     * @param string
     * @return byte
     */
    public static byte[] hexStringToBytes(String str) {  
        if (str == null || str.equals("")) {  
            return null;  
        }  
        str = str.toUpperCase();  
        int length = str.length() / 2;  
        char[] hexChars = str.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }  
        return d;  
    } 
    
    /** 
     * Convert char to byte 
     * @param char 
     * @return byte 
     */  
    private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    }  
    
    private static byte[] mapToByte(Map<String, byte[]> map){
    	Gson gson = new Gson();
    	String str = gson.toJson(map,  new TypeToken<Map<String, byte[]>>(){}.getType());
    	return str.getBytes();
    }
    
    private static Map<String, byte[]> mapFromByte(byte[] b){
    	String json = new String(b);
    	Gson gson = new Gson();
    	Map<String, byte[]> map = gson.fromJson(json, new TypeToken<Map<String, byte[]>>(){}.getType());
    	return map;
    }
    
    private static String byteToString(byte[] b){
		String str = new String(b);
    	return str;
    }
    
    private static byte[] stringToByte(String string){
		byte[] b = string.getBytes();
    	return b;
    }
    
	public static void main(String[] args) throws Exception {
	       
		 	String passwordToHash = "password";
	        String salt = getSalt();
	        System.out.println(salt);
	        String securePassword = getSaltHash(passwordToHash, salt);
	        System.out.println(securePassword);
	        System.out.println(CryptoUtils.bytesToHexString(salt.getBytes()) +" "+ securePassword);
	        
	        String securepassword = CryptoUtils.bytesToHexString(salt.getBytes())+" "+securePassword;

	        String[] section = securepassword.split(" ");
	        String correctsalt = section[0];
	        String correctpwd = section[1];
	        
	        String password = getSaltHash("password", new String(CryptoUtils.hexStringToBytes(correctsalt)));
        	System.out.println(password);
        	System.out.println(correctpwd);
        	
        	if(password.equals(correctpwd)){
            	System.out.println("correct!\n");
        	}
        	
        	System.out.println("DH key exchange \n");
        	Map<String, Key> mapA = CryptoUtils.generateDHKey();
        	Key pubkeyA = mapA.get("public_key");
        	Key prikeyA = mapA.get("private_key");
        	
        	Map<String, Key> mapB = CryptoUtils.generateDHKey(pubkeyA.getEncoded());
        	Key pubkeyB = mapB.get("public_key");
        	Key prikeyB = mapB.get("private_key");
        	
        	byte[] sessionKeyA = CryptoUtils.generateSessionKey(pubkeyA.getEncoded(), prikeyB.getEncoded());
        	byte[] sessionKeyB = CryptoUtils.generateSessionKey(pubkeyB.getEncoded(), prikeyA.getEncoded());

        	String test1 = "test dh exchange ";
        	byte[] cipher1 = CryptoUtils.encryptByAES(test1.getBytes(), sessionKeyA);
        	System.out.println("\n" + new String(cipher1));
        	byte[] cipher2 = CryptoUtils.decryptByAES(cipher1, sessionKeyB);
        	System.out.println("\n"+ new String(cipher2));
        	
        	String test2 = "test dh exchange hello ";
        	byte[] cipher3 = CryptoUtils.encryptByAES(test2.getBytes(), sessionKeyB);
        	System.out.println("\n" + new String(cipher3));
        	byte[] cipher4 = CryptoUtils.decryptByAES(cipher3, sessionKeyA);
        	System.out.println("\n"+ new String(cipher4));
        	
        	//generate a AES key from password
	        String pwd = getSaltHash("passdfswdddddddddord", salt);
	        
	        Key key = CryptoUtils.generateKeyFromPassword(pwd.getBytes());
	    	byte[] cipher5 = CryptoUtils.encryptByAES("password key".getBytes(), key);
        	byte[] cipher6 = CryptoUtils.decryptByAES(cipher5, key);
        	System.out.println("\n"+ new String(cipher6));
        	
        	
        	
        	//RSA 加密解密
        	byte[] data = CryptoUtils.encryptByRSAPrivateKey("hello world".getBytes(), CryptoUtils.getPrivateKey("private.der"));
        	byte[] result = CryptoUtils.decryptByRSAPublicKey(data, CryptoUtils.getPublicKey("public.der"));
        	System.out.println("RSA pri- pub"+ new String(result));

        	byte[] data2 = CryptoUtils.encryptByRSAPublicKey("hello world2".getBytes(), CryptoUtils.getPublicKey("public.der"));
        	byte[] result2 = CryptoUtils.decryptByRSAPrivateKey(data2, CryptoUtils.getPrivateKey("private.der"));
        	System.out.println("RSA pri- pub"+ new String(result2));
        	
        	//message digest
        	System.out.println(new String(CryptoUtils.getMD5(cipher2), "UTF-8"));
        	System.out.println(new String(CryptoUtils.getMD5(cipher2), "UTF-8"));

        	

        	//map to bytes
        	Map<String, byte[]> map = new HashMap<String, byte[]>();
        	
        	map.put("publickey", CryptoUtils.getPublicKey("public.der").getEncoded());
        	map.put("String", test2.getBytes());
        	map.put("byte", cipher2);
        	map.put("privatekey",CryptoUtils.getPrivateKey("private.der").getEncoded());
        	
        	System.out.println("111 String get bytes is :" + test2.getBytes());
        	System.out.println("222 String is : "+ test2);
        	System.out.println("333 byte is : "+ new String(cipher2));
        	
        	byte[] wrappedata = CryptoUtils.mapToByte(map);
        	
        	//byte to map 
        	Map<String, byte[]> map2 = CryptoUtils.mapFromByte(wrappedata);
        	map2.get("key");
        	System.out.println("111 get String's byte is :"+ map2.get("String"));
        	System.out.println("222 get String to Sting is :"+ new String(map2.get("String")));
        	System.out.println("333 get byte is :" + new String(map2.get("byte")));

        	map2.get("String");
        	map2.get("byte");
        	
        	byte[] data3 = CryptoUtils.encryptByRSAPrivateKey("hello world Successful!!".getBytes(), map.get("privatekey"));
        	byte[] result3 = CryptoUtils.decryptByRSAPublicKey(data3, map2.get("publickey"));
        	System.out.println("RSA pri- pub----"+ new String(result3));

	}
}

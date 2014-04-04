package utils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class Crypto {
	
	/**
	 * get a nonce
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
	
	
}

package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import utils.CryptoUtils;
import utils.Message;

public class LoginRequest{

	private ServerMain server;
	private ServerThread serverThread;
	private static String LOGIN = "LOGIN";
	private static int WRONG_PASSWORD = 6;

	private byte[] R1 = null;
	private byte[] R2 = null;
	private byte[] R3 = null;
	private PrivateKey privateKey = null;
	private byte[] DHServerKey = null;
	private byte[] dhClientPubKey = null;
	private byte[] userPassword;
	private String username;
	private byte[] sessionkeyKas;
	private Key aesSessionKey = null;
	private Map<String, String> passwordBook;
	private Map<String, byte[]> addressBook;
	private byte[] userPort;
	
	public LoginRequest(ServerMain server, ServerThread serverthread) {
		this.server = server;
		this.serverThread = serverthread;
		passwordBook = server.getPasswordBook();
		try {
			privateKey = CryptoUtils.getPrivateKey("private.der");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message handleMessage(Message messageFromClient) throws Exception {
		Message messageToClient = new Message();
		int step = messageFromClient.getStepId();
		String dataStr = messageFromClient.getData();
		byte[] data = messageFromClient.getDataBytes();
		System.out.print("get message from someone  " + messageFromClient.getStepId() + " step");
		
		// message 1: LOGIN
		if(step == 1 && dataStr.equals(LOGIN)){
			//send IP address and random R1
			if(R1 == null){
				R1 = CryptoUtils.generateNonce();
			}
			String R1String = new String(R1);
			System.out.println(R1String);
			String message = serverThread.socket.getInetAddress() + "\n"+  R1String;
			messageToClient.setProtocolId(1);
			messageToClient.setStepId(2);
			messageToClient.setData(message);
			System.out.println("send out message 1 2 IP");
			
		}
		//message 3: Pub{key} Key{gamodp, IPa, R1, R2}
		if(step == 3){
			
			//unpack data message (1,3, key{username password gamodp r1 r2} Pub{key})
			Map<String, byte[]> datamap = new HashMap<String, byte[]>();
        	datamap = CryptoUtils.mapFromByte(data);
        	
        	//get key and cipherdata from map 
        	byte[] cipherMap = datamap.get("ciphertext");
        	byte[] secretKey = datamap.get("encryptedaeskey");
        	System.out.println("encrypted aes key is "+ new String(secretKey));
        	
        	//get AES key decrypted
        	//////Should be !!
//        	byte[] key = CryptoUtils.decryptByRSAPrivateKey(cipherAesKey, privateKey);
//        	Key secretkey = CryptoUtils.generateKeyFromPassword(key);

        	Key secretkey = CryptoUtils.generateKeyFromPassword(secretKey);
        	aesSessionKey = secretkey;
        	//get Map{username password gamodp r1 r2} decrypted
        	byte[] plaintextMap = CryptoUtils.decryptByAES(cipherMap, secretkey);
        	
			Map<String, byte[]> map = new HashMap<String, byte[]>();
        	map = CryptoUtils.mapFromByte(plaintextMap);
        	
        	//lookup in passwordbook by username 
        	username = new String(map.get("username"));
	        if(passwordBook.containsKey(username)){
	        	String[] section = passwordBook.get(username).split(":");
		        String correctSalt = section[0];
		        String correctPwd = section[1];
	        	
		        //get pwd from packet and make it into Hash{salt|password}
		        String pwd = new String(map.get("password"));
		        String loginPassword = CryptoUtils.getSaltHash(pwd, new String(CryptoUtils.hexStringToBytes(correctSalt)));
	
		        //get password, gamodp, r1, r2
		        dhClientPubKey = map.get("gamodp");
	        	R1 = map.get("R1");
	        	R2 = map.get("R2");
	        	userPort = map.get("port");
	        	
	        	if(loginPassword.equals(correctPwd)){
	        		//get password hashed with salt W = hash{R2|password}
	    			System.out.println(username + ": password correct!");
	    			byte[] salt = map.get("R2");
	    	        String password = CryptoUtils.getSaltHash(loginPassword, R2);
	    	        Key pwdaesKey = CryptoUtils.generateKeyFromPassword(password.getBytes());
	    	        
	    			//prepare gsmodp 
	            	Map<String, Key> DHServerkey = CryptoUtils.generateDHKey(dhClientPubKey);
	            	Key dhServerPubKey = DHServerkey.get("public_key");
	            	Key dhServerPriKey = DHServerkey.get("private_key");
	            	sessionkeyKas = CryptoUtils.generateSessionKey(dhClientPubKey, dhServerPriKey.getEncoded());
	            	serverThread.setAesSessionKey(aesSessionKey.getEncoded());
	    			//prepare R3
	            	R3 = CryptoUtils.generateNonce();    			
	    			
	            	//put into map
					Map<String, byte[]> map4 = new HashMap<String, byte[]>();
					map4.put("gsmodp", dhServerPubKey.getEncoded());
					map4.put("R3", R3);
					
	    			//map to listbyte
		        	byte[] plaintextMap4 = CryptoUtils.mapToByte(map4);
	
	    			//AES encrypt with W
		        	byte[] ciphertext = CryptoUtils.encryptByAES(plaintextMap4, aesSessionKey);
	//	        	byte[] ciphertext = CryptoUtils.encryptByAES(plaintextMap4, sessionkeyKas);
	
	    			//set message(1,4,cipherdata)
	    			messageToClient.setProtocolId(1);
	    			messageToClient.setStepId(4);
	   				messageToClient.setData(ciphertext);
	   				
		        	}else{
		        		System.out.println("user doesn't exist");
		        		messageToClient.setProtocolId(1);
		        		messageToClient.setStepId(6);
		        		messageToClient.setData("This password is not right");
		        	}
	        	}else{
	        		System.out.println("user doesn't exist");
	        		messageToClient.setProtocolId(1);
	        		messageToClient.setStepId(6);
	        		messageToClient.setData("user/password not right");
	        	}
		}
		if(step == 5){
			byte[] plaintext = CryptoUtils.decryptByAES(data, aesSessionKey);
			System.out.println(username + ": login , finish authentication");
			String str = new String(plaintext);
			System.out.println("r3 is "+ new String(R3));
			System.out.println("str is "+ new String(str));
			
			if(str.equals(new String(R3))){
				String message = "Login successfully";
				
				//add user to the authenticated users list
				HashMap<String, byte[]> userlist = new HashMap<String, byte[]>();
				HashMap<String, byte[]> addressBook = new HashMap<String, byte[]>();

				userlist = server.getUserList();
				userlist.put(username, aesSessionKey.getEncoded());
				server.setUserList(userlist);
				serverThread.setClientName(username);
				
				addressBook = server.getUserAddress();
				addressBook.put(username, userPort);
				server.setUserAddress(addressBook);;
				/*
				//add user with its port number assigned
				HashMap<String, Integer> userPort = new HashMap<String, Integer>();
				userPort = server.getUserAddress();
				
				int port = 9500;
				while(userPort.values().contains(port)){
					++port;
				}
				userPort.put(username, port);
				server.setUserAddress(userPort);
				*/
				
				messageToClient.setProtocolId(1);
				messageToClient.setStepId(9);
				messageToClient.setData("");

				//print all users
				for(String s: userlist.keySet()){
					 System.out.println(s);
				}
				
			}
		}
	   return messageToClient;
	}
	
}

package client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import server.ServerThread;
import utils.CryptoUtils;
import utils.Message;
import utils.MessageReader;

/**
 * communicate with server
 * @author yaweisun
 *
 */
public class ServerListener extends Thread{
	
	public ClientMain client;
	public Socket socket;
	
//	private BufferedReader inputRead;
	private Message messageToServer;
	private Message messageFromServer;
	
	private BufferedReader in;
	private PrintWriter out;
	private String R1;
	//128-bits salt randomR2
	private String R2;
	private byte[] R3;
	private PublicKey publicKey = null;
	private static int WRONG_PASSWORD = 6;
	private Map<String, Key> dhKeyMap;
	private String password = null;
	private Key aesSessionKey;
	private boolean isAuthenticated = false;
	private LocalListener localListener = null;
	private String userName;
	
	public ServerListener(ClientMain client, Socket socket) {
		try{
		messageToServer = new Message();
		this.socket = socket;
		this.client = client;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
		
		R1 = null;
		R2 = null;

		try {
			dhKeyMap = CryptoUtils.generateDHKey();
			publicKey = CryptoUtils.getPublicKey("public.der");
		} catch (Exception e) {
			e.printStackTrace();
		}
		start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {

		try {

			// 1. send "LOGIN"
			messageToServer.setProtocolId(1);
			messageToServer.setStepId(1);
			messageToServer.setData("LOGIN");
			String str = MessageReader.messageToJson(messageToServer);
			out.println(str);
			System.out.println(client.getPort());
			
			// 2. wait until receive {IP of local, Random R1}
			messageFromServer = MessageReader.getMessageFromStream(in);
	    	System.out.print("Get Message("+ messageFromServer.getProtocolId()+", "+ messageFromServer.getStepId() + ")");

			if(messageFromServer.getProtocolId() == 1 && messageFromServer.getStepId() == 2){
				//get R1
				InputStream is = new ByteArrayInputStream(messageFromServer.getDataBytes());
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				String IpAddress = br.readLine();
				R1 = br.readLine();
				is.close();
				br.close();
			}else{
				System.out.println("Didn't get message(1,1,data), ipa and r1");
			}
			
			//3. send password to prove identity
			int counter = 0;
			System.out.println("USERNAME:");
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String inputname = br.readLine();
			
			while(counter < 3 ){
				
				if(R1 == null){
					//just leave it
					R1 = CryptoUtils.getSalt();
				}
				//prepare R2
				R2 = CryptoUtils.getSalt();
				
				//prepare DH key
	        	Key publickeyA = dhKeyMap.get("public_key");
	        	
	        	//prepare a random byte 
	        	byte[] secretkey = CryptoUtils.generateNonce();
	        	//generate a aes key
	        	Key aeskey = CryptoUtils.generateKeyFromPassword(secretkey);
	        	aesSessionKey = aeskey;
	        	
	        	//encrypt byte with public key
	        	byte[] encryptedSecretKey = CryptoUtils.encryptByRSAPublicKey(secretkey, publicKey);
	        	
	        	//get password
				System.out.println("Password:");
	        	password = br.readLine();
	            
				//put elements into map
	        	Map<String, byte[]> map = new HashMap<String, byte[]>();
	        	map.put("username", inputname.getBytes());
	        	map.put("password", password.getBytes());
	        	map.put("gamodp", publickeyA.getEncoded());
	        	map.put("R1", R1.getBytes());
	        	map.put("R2", R2.getBytes());
	        	int port = client.getPort();
	        	String portString = String.valueOf(port);
	        	byte[] portByte = portString.getBytes();
	        	
	        	map.put("port", portByte);
	        
	        	byte[] plaintextMap = CryptoUtils.mapToByte(map);
	        	

	        	//encrypt plaintext with temporary aes key
	        	byte[] ciphertextMap = CryptoUtils.encryptByAES(plaintextMap, aeskey);
	        	
	        	Map<String, byte[]> msgdatamap = new HashMap<String, byte[]>();
	        	msgdatamap.put("ciphertext", ciphertextMap);
	        	msgdatamap.put("encryptedaeskey", secretkey);
	        	//This should not be!!!
//	        	msgdatamap.put("encryptedaeskey", encryptedSecretKey);

	        	//////////now test here////////////
	        	/*
	        	msgdatamap.get("encryptedaeskey");
	        	Key privatekey = CryptoUtils.getPrivateKey("private.der");
	        	byte[] key = CryptoUtils.decryptByRSAPrivateKey(encryptedAESKey, privatekey);
	        	System.out.println(new String(key));
	        	byte[] k = (new String(key)).getBytes();
	        	if(key.equals(nonce)){
	        		System.out.println("hello is equals to hello");
	        	}
	        	Key secretkey = CryptoUtils.generateKeyFromPassword(msgdatamap.get("encryptedaeskey"));
	        	byte[] plMap = CryptoUtils.decryptByAES(ciphertextMap, secretkey);
    			*/
	        	
	        	//set message (1,3, key{username password gamodp r1 r2} Pub{key})
	        	messageToServer.setData(CryptoUtils.mapToByte(msgdatamap));
				messageToServer.setProtocolId(1);
				messageToServer.setStepId(3);
				
				String pwdPacket = MessageReader.messageToJson(messageToServer);
				out.println(pwdPacket);
				
				//  wait until receive message(1,4, DHkey)
				messageFromServer = MessageReader.getMessageFromStream(in);
		    	System.out.print("Get Message("+ messageFromServer.getProtocolId()+", "+ messageFromServer.getStepId() + ")");

				if(messageFromServer.getProtocolId() == 1 && messageFromServer.getStepId() == 4){
					userName = inputname;
//					br.close();
					break;
				}
				if (messageFromServer.getProtocolId() == 1 && messageFromServer.getStepId() == WRONG_PASSWORD) {
					if(counter == 0){
			        	System.out.println("Incorrect password, try again");
		        	}
		        	if(counter == 1){
			        	System.out.println("Incorrect password, 2 more times to try");
		        	}
		        	if(counter == 2){
			        	System.out.println("Incorrect password, 1 more times to try");
		        	}
		        	if(counter == 3){
		        		System.out.println("You have tryied 3 time");
		        		System.out.println("Sorry...");
		        		br.close();
		        		socket.close();
		        		//!!!!!!!!!!!!!!!!ADD 
		        		////////////remember this user name and save the timestamp
		        		////////////
		        	}
				}
	        	counter++;
			}
				
			if (messageFromServer.getProtocolId() == 1 && messageFromServer.getStepId() == 4) {

//				System.out.println("Correct Password...");
			    System.out.println("Receiving gsmodp from server...");
	       
		        //prepare password hashed with salt W = hash{R2|password}
		    	String pwd = CryptoUtils.getSaltHash(password, R2);
    	        Key pwdaesKey = CryptoUtils.generateKeyFromPassword(pwd.getBytes());
				byte[] b = messageFromServer.getDataBytes();
    	        //unpack incoming message
				
				///this session key is not working 
//    	        byte[] ciphermap2 = CryptoUtils.decryptByAES(b, pwdaesKey);
    	        byte[] ciphermap2 = CryptoUtils.decryptByAES(b, aesSessionKey);
//    	        System.out.println(new String(b));
    	        
				Map<String, byte[]> map = new HashMap<String, byte[]>();
				map = CryptoUtils.mapFromByte(ciphermap2);
				
				byte[] dhServerPubkey = null;
				dhServerPubkey = map.get("gsmodp");
		        R3 = map.get("R3");

		        //generate gasmodp session key and SAVE
            	Key dhClientPriKey = dhKeyMap.get("private_key");
            	byte[] sessionkeyKas = CryptoUtils.generateSessionKey(dhServerPubkey, dhClientPriKey.getEncoded());

				//encrypt r3 with key kas
    	        byte[] finalmsg = CryptoUtils.encryptByAES(R3, aesSessionKey);

				//set message
            	messageToServer.setProtocolId(1);
    			messageToServer.setStepId(5);
   				messageToServer.setData(finalmsg);
   				String pwdPacket = MessageReader.messageToJson(messageToServer);
				out.println(pwdPacket);
   				System.out.println("Sending server the end  Kas{R3}");
		    	
   				messageFromServer = MessageReader.getMessageFromStream(in);
		    	System.out.print("Get Message("+ messageFromServer.getProtocolId()+", "+ messageFromServer.getStepId() + ")");
//		    	int port = Integer.parseInt(messageFromServer.getData());
   				client.setIsAuthenticated(true);
		    	client.setSessionKeyKas(aesSessionKey.getEncoded());
//   			client.setListenerPort(port);
			}
			
			System.out.println("");
			
			/**
			 * after first login step, the user log into the group
			 */
			if(client.getIsAuthenticated() == true){
				//go to list/logout/chat protocols
				System.out.println("Finish authendication!");
				handleRequest();
			}else{
				System.out.println("Need to authenticate with server");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleRequest() throws Exception {
		BufferedReader inputRead = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			System.out.println("chat/list/logout?");
		    String command = inputRead.readLine();
		    if(command.equalsIgnoreCase("list")){
		    	 System.out.println("Here is the friend list");
		    	 System.out.println("logout protocol");
		    	 messageToServer.setProtocolId(2);
	    		 messageToServer.setStepId(1);
	   		     byte[] listMessage = CryptoUtils.encryptByAES("LIST".getBytes(), aesSessionKey);
	   			 messageToServer.setData(listMessage);

		    	 //send a message to server kas{"list", cookie}
	   			 String packet = MessageReader.messageToJson(messageToServer);
				 out.println(packet);
		    	 System.out.print("Send Message("+ messageToServer.getProtocolId()+", "+ messageToServer.getStepId() + ")  ");
		    	 
		    	 //receive feed back from server kas{map<String, String> userlist, cookies}
		    	 messageFromServer = MessageReader.getMessageFromStream(in);
		    	 System.out.print("Get Message("+ messageFromServer.getProtocolId()+", "+ messageFromServer.getStepId() + ")");
		    
		    }

		    if(command.equalsIgnoreCase("chat")){
		    	 
		    	 System.out.println("Sure!");
		    	 System.out.println("Who do you want to chat with?");
		    	 
		    	 String receiverName = inputRead.readLine();
		    	 byte[] ciphertext = CryptoUtils.encryptByAES(receiverName.getBytes(), aesSessionKey);
		    	 //send server a request 
		    	 messageToServer.setProtocolId(3);
	    		 messageToServer.setStepId(1);
//	   			 messageToServer.setData("message for chating"+ userName + "want to talk to "+ receiverName);
	   			 messageToServer.setData(ciphertext);

		    	 //send a message to server kas{userName, receiverName, R4}
	   			 String packet = MessageReader.messageToJson(messageToServer);
				 out.println(packet);
		    	 System.out.print("Send Message("+ messageToServer.getProtocolId()+", "+ messageToServer.getStepId() + ")  ");
		    	 
		    	 //receive feed back from server 
		    	 messageFromServer = MessageReader.getMessageFromStream(in);
		    	 System.out.print("Get Message("+ messageFromServer.getProtocolId()+", "+ messageFromServer.getStepId() + ")");

		    	 if(messageFromServer.getProtocolId() == 3 && messageFromServer.getStepId() == 2){
		    		 //get server's response that I can talk to receiver
		    		 
		    		 //unpack the message Kas{Kbs{Kab, A}, B, B's portnumber, Kab, R1}
		    		 
		    		 //get Ticket-to-b = Kbs{Kab, A} and save
		    		 
		    		 //get B and verify if B is true or not
		    		 
		    		 //get Kab and save
		    		 
		    		 //get R1
		    		 
		    		 //initialize a new socket and send something to receiverName the ticket
		    		 //新开一个socket 准备 给作为ServerSocket的收信人发送消息
		    		    Message messageToClient = new Message();
		    		    Message messageFromClient = new Message();
			    		String	hostAddress = "127.0.0.1";
			    		
			    		byte[] portbyte = messageFromServer.getDataBytes();
			    		byte[] plainbyte = CryptoUtils.decryptByAES(portbyte, aesSessionKey);
			    		String portString = new String(plainbyte);
			        	int port = Integer.parseInt(portString);
			        	System.out.println("the Port# of client: "+port);
			        	System.out.println("start a socket at dest. port: "+ port);

//			            br.close();
			            //暂时从控制台输入端口信息
			        	
			    		Socket inviteSocket = new Socket(hostAddress, port);
			    		BufferedReader inviteBr = new BufferedReader(new InputStreamReader(inviteSocket.getInputStream()));
			    		PrintWriter invitePw = new PrintWriter(inviteSocket.getOutputStream(), true);
			    		messageToClient.setProtocolId(3);
			    		messageToClient.setStepId(3);
			    		messageToClient.setData("message for chating"+ userName + "want to talk to "+ receiverName);

				    	//send a message to receiver peerlistener kas{N2} Kbs{Kab, A}
			   			String messageToB = MessageReader.messageToJson(messageToClient);
			   			invitePw.println(messageToB);
				    	System.out.print("Send Message("+ messageToClient.getProtocolId()+", "+ messageToClient.getStepId() + ")  ");
				    	 
				    	//receive from peerlistener 
				    	messageFromClient = MessageReader.getMessageFromStream(inviteBr);
				    	System.out.println("Get Message("+ messageFromClient.getProtocolId()+", "+ messageFromClient.getStepId() + ")");

			    		//send message 
			    		System.out.println("Connecting<sender, receiver>! <"+userName + " : "+receiverName+"> ");
			    		
			    		if(messageFromClient.getProtocolId() == 3 && messageFromClient.getStepId() == 4){
			    			System.out.println("start a new connection");
			    		}
			    		
			    		messageToClient.setProtocolId(3);
			    		messageToClient.setStepId(5);
			    		messageToClient.setData("Ka'b' established");
			    		//send a message to receiver peerlistener kas{ga'modp}
			   			messageToB = MessageReader.messageToJson(messageToClient);
			   			invitePw.println(messageToB);
				    	System.out.println("Send Message("+ messageToClient.getProtocolId()+", "+ messageToClient.getStepId() + ")");
				    	
		    		 
		    	 }else if(messageFromServer.getProtocolId() == 3 && messageFromServer.getStepId() == 3){
		    		//this user is busy
		    		System.out.println("User is busy");
		    	 }else{
		    		 //wrong message 
		    		 System.out.println("The user:" + receiverName + " is currently not available");
		    	 }
		    	 
		     }
		     if(command.equalsIgnoreCase("logout")){
		    	 System.out.println("logout protocol");
		    	 messageToServer.setProtocolId(5);
	    		 messageToServer.setStepId(1);
	    		 byte[] listMessage = CryptoUtils.encryptByAES("logout".getBytes(), aesSessionKey);
		   		 messageToServer.setData(listMessage);
		    	 
		   		 //send a message to server kas{"logout", cookie}
	   			 String packet = MessageReader.messageToJson(messageToServer);
				 out.println(packet);
		    	 System.out.print("Send Message("+ messageToServer.getProtocolId()+", "+ messageToServer.getStepId() + ")  ");
		    	 
		    	 //receive feed back from server kas{"confirm"}
		    	 messageFromServer = MessageReader.getMessageFromStream(in);
		    	 System.out.print("Get Message("+ messageFromServer.getProtocolId()+", "+ messageFromServer.getStepId() + ") ");
		    	 if(messageFromServer.getProtocolId()==5 && messageFromServer.getStepId()==2){
		    		 System.out.println("successfully logout!");
		    		 //kill this socket
		    		 
		    		 //kill the peerListener thread
		    		 
		    	 }
		    	 client.setIsAuthenticated(false);
		    	 break;
			}
			if ((command.equalsIgnoreCase("logout")
				|| command.equalsIgnoreCase("list")
				|| command.equalsIgnoreCase("chat")) == false){
				
//				socket.close();
				System.out.println("wrong input...");
			}
		}
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
	

}

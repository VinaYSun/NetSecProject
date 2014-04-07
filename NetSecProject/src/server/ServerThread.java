package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import utils.Message;
import utils.MessageReader;

/**
 * For each socket connection from client, open up a thread to handle this socket
 */
public class ServerThread extends Thread{
	
	public ServerMain server;
	public Socket socket; 
	private Message messageFromClient;
	private Message messageToClient;
	private static final int LOGIN_AUTH = 1;
	private static final int LIST_REQUEST = 2;
	private static final int CHAT_REQUEST = 3;
	private static final int LOGOUT_REQUEST = 4;
	private BufferedReader in;
	private PrintWriter out;
	private LoginRequest loginRequest = null;
	private ListRequest listRequest = null;
	private LogoutRequest logoutRequest = null;
	private ChatRequest chatRequest = null;
	private byte[] aesSessionKey = null;

	public ServerThread(ServerMain server, Socket socket){
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			handleSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 1.receive message from client
	 * 2.send reply message
	 * @throws Exception 
	 */
	private void handleSocket() throws Exception {
		
		try {

			 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	         out = new PrintWriter(socket.getOutputStream(), true);
	        
	         while(true){
		         
		         String temp = MessageReader.readInputStream(in);
		         if(temp.equals("")) continue;
		        
		         messageFromClient = MessageReader.messageFromJson(temp);
		         
		         switch (messageFromClient.getProtocolId()){
		        	 case LOGIN_AUTH:
		        		 if(loginRequest == null){
		        			 loginRequest = new LoginRequest(server, this);
		        		 }
		        		 messageToClient = loginRequest.handleMessage(messageFromClient);
		        		 break;
		        	 case LIST_REQUEST:
		        		 if(listRequest == null){
		        			 listRequest = new ListRequest(server, socket);
		        		 }
		        		 messageToClient = listRequest.handleMessage(messageFromClient);
		        		 break;
		        	 case CHAT_REQUEST:
		        		 if(chatRequest == null){
		        			 chatRequest = new ChatRequest(server, socket);
		        		 }
		        		 messageToClient = chatRequest.handleMessage(messageFromClient);
		        		 break;
		        	 case LOGOUT_REQUEST:
		        		 if(logoutRequest == null) {
		         		 logoutRequest = new LogoutRequest(server, socket);
		        		 }
		        		 messageToClient = logoutRequest.handleMessage(messageFromClient);
		        		 break;
		         }
		        	 
				 String str = MessageReader.messageToJson(messageToClient);
		         //send message;
		         out.println(str);
			 }
//	         socket.close(); 
	         
	    } catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public ServerMain getServer() {
		return server;
	}

	public void setServer(ServerMain server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Message getMessageFromClient() {
		return messageFromClient;
	}

	public void setMessageFromClient(Message msgFromClient) {
		this.messageFromClient = msgFromClient;
	}

	public byte[] getAesSessionKey() {
		return aesSessionKey;
	}

	public void setAesSessionKey(byte[] aesSessionKey) {
		this.aesSessionKey = aesSessionKey;
	}

}
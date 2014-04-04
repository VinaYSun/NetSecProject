package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import utils.Message;
import utils.MessageReader;

/**
 * For each socket connection from client, open up a thread to handle this socket
 */
public class ServerThread extends Thread{
	
	private ServerMain server;
	private Socket socket; 
	private Message messageFromClient;
	private Message messageToClient;
	private static final int LOGIN_AUTH = 1;
	private static final int LIST_REQUEST = 2;
	private static final int TICKET_REQUEST = 3;
	private static final int LOGOUT_REQUEST = 4;
	private BufferedReader in;
	private PrintWriter out;
	
	public ServerThread(ServerMain server, Socket socket){
		this.server = server;
		this.socket = socket;
		this.messageFromClient = new Message();
		this.messageToClient = new Message();
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
	 */
	private void handleSocket() {
		
		try {

			 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	         out = new PrintWriter(socket.getOutputStream(), true);
	        
	         int counter = 0;
	         
	         while(true){
	         
	         String temp = MessageReader.readInputStream(in);
	         if(temp.equals("")) continue;
	        
	         messageFromClient = MessageReader.messageFromJson(temp);
	         String msgData = new String(messageFromClient.getDataBytes(), "UTF-8");
	         System.out.println("From client:"+msgData);
	         
	       //determine the feedback message
	         switch (messageFromClient.getProtocolId()){
	        	 case LOGIN_AUTH:
	        		 LoginRequest login = new LoginRequest(server, socket, messageFromClient);
	        		 messageToClient = login.getMsgToClient();
	        		 break;
	        	 case LIST_REQUEST:
	        		 new ListRequest();
	        		 msgData = "LIST protocol";
	        		 break;
	        	 case TICKET_REQUEST:
	        	     new TicketRequest();
	        		 msgData = "Tickets protocol";
	        		 break;
	        	 case LOGOUT_REQUEST:
	        		 new LogoutRequest();
	        		 msgData = "Log out protocol";
	        		 break;
	         }
	        	 
			 String str = MessageReader.messageToJson(messageToClient);
	         counter++;
	         //printwriter write in message;
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

	
}
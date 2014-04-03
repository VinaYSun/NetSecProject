package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import utils.Message;
import utils.MessageConverter;

/**
 * For each socket connection from client, open up a thread to handle this socket
 */
public class ServerThread extends Thread{
	
	private ServerMain server;
	private Socket socket; 
	private Message msgFromClient;
	private Message msgToClient;
	private static final int LOGIN_AUTH = 1;
	private static final int LIST_REQUEST = 2;
	private static final int TICKET_REQUEST = 3;
	private static final int LOGOUT_REQUEST = 4;
	
	public ServerThread(ServerMain server, Socket socket){
		this.server = server;
		this.socket = socket;
		this.msgFromClient = new Message();
		this.msgToClient = new Message();
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
			 System.out.println("Connected:!RemotePort:" + socket.getPort() + "//LocalPort:" + socket.getLocalPort());
			 
	         BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
	         String inputstream = readInputStream(br, socket);
	         msgFromClient= new MessageConverter().messageFromJson(inputstream);

	         String tempData = new String(msgFromClient.getData(), "UTF-8");
	         System.out.println(tempData);
	         
	         int protocolId = msgFromClient.getProtocolId();
	         
	         //determine the feedback message
	         switch (protocolId){
	        	 case LOGIN_AUTH:
	        		 LoginRequest login = new LoginRequest(server, socket);
	        		 login.verify(tempData);
	        		 msgToClient = login.getMsgToClient();
	        		 break;
	        	 case LIST_REQUEST:
	        		 new ListRequest();
	        		 break;
	        	 case TICKET_REQUEST:
	        	     new TicketRequest();
	        		 break;
	        	 case LOGOUT_REQUEST:
	        		 new LogoutRequest();
	        		 break;
	         }
	         
	         //write response to client 
	         Writer writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8"); 
	         String tempReply = new MessageConverter().messageToJson(msgToClient);
	         writer.write(tempReply);
	         writer.write("this is a test");
	         writer.write("eof\n");  
	         writer.flush();  
	         writer.close(); 
	         br.close();
	         socket.close(); 

	    } catch (IOException e) {
			e.printStackTrace();
		} 
		
	}


	/**
	 * read data from inputStream
	 * @param socket
	 * @return inputstream in String
	 * @throws IOException
	 */
	private String readInputStream(BufferedReader br, Socket socket) throws IOException{
        StringBuilder sb = new StringBuilder();  
        String temp;  
        int index;  
        while ((temp=br.readLine()) != null) {  
           System.out.println(temp);  
  		  //read inputstream until meat end symbol "eof"
           if ((index = temp.indexOf("eof")) != -1) {
            sb.append(temp.substring(0, index));  
               break;  
           }  
           sb.append(temp);  
        }
        return sb.toString();
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
		return msgFromClient;
	}

	public void setMessageFromClient(Message msgFromClient) {
		this.msgFromClient = msgFromClient;
	}

	
}
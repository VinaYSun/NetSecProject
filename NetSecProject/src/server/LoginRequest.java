package server;

import java.net.Socket;
import java.nio.charset.Charset;

import utils.Message;

public class LoginRequest extends ServerThread{

	private Message msgFromClient;
	private Message msgToClient;
	private String inputdata;
	
	public LoginRequest(ServerMain server, Socket socket) {
		super(server, socket);
		msgFromClient = super.getMessageFromClient();
		msgToClient = new Message();
		System.out.println("login request is set up ");
	}
	
	public LoginRequest(ServerMain server, Socket socket, String data) {
		super(server, socket);
		msgFromClient = super.getMessageFromClient();
		msgToClient = new Message();
		inputdata = data;
	}
	
	public boolean verify(String tempData) {
		
		byte[] b = "This is from login protocol".getBytes(Charset.forName("UTF-8"));
	    Message msg = new Message(1,1,b);
		this.setMsgToClient(msg);
		System.out.println("reply message is written ");

		return false;
	}
	
	public Message getMsgToClient() {
		return msgToClient;
	}

	public void setMsgToClient(Message msgToClient) {
		this.msgToClient = msgToClient;
	}

}

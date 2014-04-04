package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;

import utils.Message;

public class LoginRequest extends ServerThread{

	private Message msgFromClient;
	private Message msgToClient;
	private String inputdata;
	private int stepId;
	
	public LoginRequest(ServerMain server, Socket socket, Message msg) throws UnsupportedEncodingException {
		super(server, socket);
		msgFromClient = super.getMessageFromClient();
		msgToClient = new Message();
		stepId = msg.getStepId();
		System.out.println("login request is set up ");
		verify();
	}
	
	public void verify() throws UnsupportedEncodingException {
		if(stepId == 1){
			String message = "Ip address of this client";
			this.setMessage(1, 2, message);
			System.out.println("Step2");
		}else if(stepId == 3){
			String message = "Session key establishment";
			this.setMessage(1, 4, message);
			System.out.println("Step4");

		}else if(stepId == 5){
			String message = "Login successfully";
			this.setMessage(1, 6, message);
			System.out.println("finish authentication");

		}else {
			System.out.println("Invalid message");
		}
	
	}
	
	public void setMessage(int protocolId, int stepId, String str) throws UnsupportedEncodingException{
		msgToClient.setProtocolId(protocolId);
		msgToClient.setStepId(stepId);
		msgToClient.setData(str);
		System.out.println(msgToClient.getData());
	}

	public Message getMsgToClient() {
		return msgToClient;
	}

	public void setMsgToClient(Message msgToClient) {
		this.msgToClient = msgToClient;
	}

}

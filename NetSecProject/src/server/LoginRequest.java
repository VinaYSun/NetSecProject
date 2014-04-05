package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;

import utils.CryptoUtils;
import utils.Message;

public class LoginRequest extends ServerThread{

	private Message msgFromClient;
	private Message msgToClient;
	private String inputdata;
	private int stepId;
	private Socket socket;
	
	public LoginRequest(ServerMain server, Socket socket, Message msg) throws UnsupportedEncodingException {
		super(server, socket);
		this.socket = socket;
		msgFromClient = super.getMessageFromClient();
		msgToClient = new Message();
		stepId = msg.getStepId();
//		System.out.println("login request is set up ");
		verify();
	}
	
	public void verify() throws UnsupportedEncodingException {
		if(stepId == 1){
			//get IP address
			//get Random number
			byte[] b = CryptoUtils.generateNonce();
			System.out.println(b.toString());
			String message = socket.getInetAddress() + "\n"+  b.toString();
			setMessage(msgToClient, 1, 2, message);
			
		}else if(stepId == 3){
			String message = "Session key establishment";
			setMessage(msgToClient, 1, 4, message);

		}else if(stepId == 5){
			String message = "Login successfully";
			setMessage(msgToClient, 1, 6, message);
			System.out.println("finish authentication");

		}else {
			System.out.println("Invalid message");
		}
	
	}
	
	public Message getMsgToClient() {
		return msgToClient;
	}

	public void setMsgToClient(Message msgToClient) {
		this.msgToClient = msgToClient;
	}
	
	public void setMessage(Message message, int protocolId, int stepId, String str) throws UnsupportedEncodingException{
		message.setProtocolId(protocolId);
		message.setStepId(stepId);
		message.setData(str);
	}

}

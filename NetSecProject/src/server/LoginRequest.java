package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import utils.CryptoUtils;
import utils.Message;

public class LoginRequest extends ServerThread{

	private Message msgFromClient;
	private Message msgToClient;
	private String inputdata;
	private int stepId;
	private Socket socket;
	
	public LoginRequest(ServerMain server, Socket socket, Message msg) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		super(server, socket);
		this.socket = socket;
		msgFromClient = super.getMessageFromClient();
		msgToClient = new Message();
		stepId = msg.getStepId();
		verify();
	}
	
	public void verify() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		if(stepId == 1){
			//get IP address
			//get Random number
			byte[] b = CryptoUtils.generateNonce();
			System.out.println(b.toString());
			String message = socket.getInetAddress() + "\n"+  b.toString();
			msgToClient.setProtocolId(1);
			msgToClient.setStepId(2);
			msgToClient.setData(message);
			
		}else if(stepId == 3){
			String message = "Session key establishment";
			msgToClient.setProtocolId(1);
			msgToClient.setStepId(4);
			msgToClient.setData(message);

		}else if(stepId == 5){
			String message = "Login successfully";
			msgToClient.setProtocolId(1);
			msgToClient.setStepId(5);
			msgToClient.setData(message);
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
	
}

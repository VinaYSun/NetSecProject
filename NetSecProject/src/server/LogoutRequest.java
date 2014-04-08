package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;

import utils.CryptoUtils;
import utils.Message;

public class LogoutRequest {
	
	private ServerThread serverThread;
	private ServerMain server;
	private HashMap<String, byte[]> userlist = null;
	private String userName;
	private byte[] sharedKey;
	
	public LogoutRequest(ServerMain server, ServerThread serverThread) {
		this.server = server;
		this.serverThread = serverThread;
		try {
			userlist = new HashMap<String, byte[]>();
			userlist = server.getUserList();
			userName = serverThread.getClientName();
			sharedKey = userlist.get(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message handleMessage(Message messageFromClient) throws Exception {
		Message messageToClient = new Message();
		int step = messageFromClient.getStepId();
		System.out.print("get message(" +  messageFromClient.getProtocolId() +" , " +messageFromClient.getStepId() + " )");
		
		if(step == 1){
			byte[] data = messageFromClient.getDataBytes();
			byte[] plaintext = CryptoUtils.decryptByAES(data, sharedKey);
			String textString = new String(plaintext);
			if(textString.equalsIgnoreCase("logout")){
				messageToClient.setData("Kas{confirm}");
				messageToClient.setProtocolId(5);
				messageToClient.setStepId(2);
				System.out.print("send message(" +  messageToClient.getProtocolId() +" , " +messageToClient.getStepId() + " )");
			}
		}
		return messageToClient;

	}

}

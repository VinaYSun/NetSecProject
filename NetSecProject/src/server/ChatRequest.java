package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;

import utils.CryptoUtils;
import utils.Message;

public class ChatRequest {

	private ServerMain server;
	private ServerThread serverThread;
	private String userName;
	private byte[] sharedKey;
	private HashMap<String, byte[]> userlist = null;
	private HashMap<String, byte[]> addressBook = null;
	
	public ChatRequest(ServerMain server, ServerThread serverThread) {
		this.server = server;
		this.serverThread = serverThread;
		try {
			userlist = new HashMap<String, byte[]>();
			userlist = server.getUserList();
			addressBook = new HashMap<String, byte[]>();
			addressBook = server.getUserAddress();
			userName = serverThread.getClientName();
			sharedKey = userlist.get(userName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Message handleMessage(Message messageFromClient) throws Exception {
		Message messageToClient = new Message();
		int step = messageFromClient.getStepId();
		String dataStr = messageFromClient.getData();
		byte[] data = messageFromClient.getDataBytes();
		System.out.print("get message(" +  messageFromClient.getProtocolId() +" , " +messageFromClient.getStepId() + " )");
		
		//unpack 
		//prepare port number
		byte[] port = null;
//		String receiverName = new String(dataStr);
		//check if the user is avalible
		
		if(step == 1){
			byte[] plaintext = CryptoUtils.decryptByAES(data, sharedKey);
			String textString = new String(plaintext);
			if(addressBook.containsKey(textString)){
				port = addressBook.get(textString);
				System.out.println("server has "+textString+" with port number"+new String(port));

				messageToClient.setData("ticket-to-B and Kas{Kab, B's port}");
				byte[] cipher = CryptoUtils.encryptByAES(port, sharedKey);
				messageToClient.setData(cipher);
				messageToClient.setProtocolId(3);
				messageToClient.setStepId(2);
				System.out.print("send message(" +  messageToClient.getProtocolId() +" , " +messageToClient.getStepId() + " )");
			}
		}
		return messageToClient;
	}
}



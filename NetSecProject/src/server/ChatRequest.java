package server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;

import utils.Message;

public class ChatRequest {
	private Socket socket;
	private ServerMain server;

	public ChatRequest(ServerMain server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	public Message handleMessage(Message messageFromClient) throws UnsupportedEncodingException {
		Message messageToClient = new Message();
		int step = messageFromClient.getStepId();
		String dataStr = messageFromClient.getData();
		byte[] data = messageFromClient.getDataBytes();
		
//		messageToClient.setProtocolId(3);
//		messageToClient.setStepId(3);
//		messageToClient.setData("This user is busy...try later");
		//check if the user is avalible
		if(true){
			messageToClient.setData("{Kbs{Kab, A},B,R1,Kab}Kas");
			messageToClient.setProtocolId(3);
			messageToClient.setStepId(2);
			System.out.print("Send Message("+ messageToClient.getProtocolId()+", "+ messageToClient.getStepId() + ")");
		}
		
		return messageToClient;
	}
	
	
}



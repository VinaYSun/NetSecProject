package server;

import java.net.Socket;

import utils.Message;

public class LogoutRequest {
	
	private Socket socket;
	private ServerMain server;
	
	public LogoutRequest(ServerMain server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	public Message handleMessage(Message messageFromClient) {
		// TODO Auto-generated method stub
		return null;
	}

}

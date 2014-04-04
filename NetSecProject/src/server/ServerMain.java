package server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Map;

import utils.Message;

public class ServerMain {
	
	public Map<String, String> userMap;
	public ServerSocket serverSocket;
	
	
	public ServerMain(int port) throws IOException {

		try {
			initialize();
			serverSocket = new ServerSocket(port);
			while (true) {
				Socket client;
				client = serverSocket.accept();
				new ServerThread(this, client).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			this.serverSocket.close();
		}
	}
	
	private void initialize() {
		
		loadPrivateKey();
		loadUser();
	}

	private void loadUser() {

	}

	private void loadPrivateKey() {
		
	}
	
	public static void main(String args[]){

		 try {
			new ServerMain(8899);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
	
	
		public Map<String, String> getUserMap() {
			return userMap;
		}

		public void setUserMap(Map<String, String> userMap) {
			this.userMap = userMap;
		}

		public ServerSocket getServerSocket() {
			return serverSocket;
		}

		public void setServerSocket(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}

}

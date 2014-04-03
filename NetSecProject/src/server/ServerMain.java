package server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Map;

import utils.Message;

public class ServerMain {
	
	public Map<String, String> userMap;
	public ServerSocket serverSocket;
	
	
	public ServerMain(int port) throws IOException{
		
		serverSocket = new ServerSocket(port);  	
		initialize();
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

		 try{
		 ServerMain server = new ServerMain(8899);
		 
		 while (true) {  
			 //start a new thread until serversocket accept a socket for communicating with client
	         new ServerThread(server, server.getServerSocket().accept()).start();  
	         System.out.print("Start a new thread");
	         
	         }  
		 }catch(IOException e){
			 
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

package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import utils.Message;
import utils.MessageReader;


public class ClientMain {
	
	public ServerSocket serverListener;
	public Socket socket;
	public Map<String, List> peers; 
	public Map<String, List> dialogList;
	private int port;
	private String hostAddress;
	private Message messageToServer;
	private Message messageFromServer;
	private BufferedReader in;
	private PrintWriter out;
	private MessageReader messageReader;
	private Boolean isAuthenticated;
	
	public ClientMain(){
		
		try {

			initial();
			//talking to server
	        new ServerListener(this, this.socket);
	        //talking with peer(initiator)
	        
	        if(isAuthenticated){
	        	new LocalListener();
	        	//connecting from peer(recipient)
	        	while(true){
	        		new PeerListener();
	        	}
	        }else{
	        	System.out.println("Login authentication didn't finish");
	        }

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e){
			System.out.println("Cannot connect to server.");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void initial() throws UnknownHostException, IOException, ConnectException {
		messageReader = new MessageReader();
		messageToServer = new Message();
		hostAddress = "127.0.0.1";
		port = 8899;
		
		isAuthenticated = false;
		socket = new Socket(hostAddress, port);
	}

	public static void main(String args[]){
		new ClientMain();
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Map<String, List> getPeers() {
		return peers;
	}

	public void setPeers(Map<String, List> peers) {
		this.peers = peers;
	}
	
	public Boolean getIsAuthenticated() {
		return isAuthenticated;
	}

	public void setIsAuthenticated(Boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
}

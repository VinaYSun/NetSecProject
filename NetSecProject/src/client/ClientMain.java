package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;


public class ClientMain {
	
	public ServerSocket serverListener;
	public Socket socket;
	public Map<String, List> peers; 
	public Map<String, List> dialogList;
	private int port;
	private String hostAddress;
	
	public ClientMain(){
		try {
			//需要后期载入！
			hostAddress = "127.0.0.1";
			port = 8899;
			
			socket = new Socket(hostAddress, port);
			System.out.println("Connected:!RemotePort:" + socket.getPort() + "//LocalPort:" + socket.getLocalPort());

			socket.close();
			//start at the beginning, listen to client input and send message to server
//		    	new LocalListener(socket).start();
		    	
		    //start a thread response to server message once accepted
//		    	new ServerListener(socket).start();
				
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e){
			System.out.println("Cannot connect to server.");
		} catch (IOException e) {
			e.printStackTrace();
		} 
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

}

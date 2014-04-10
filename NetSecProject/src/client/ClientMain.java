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

import server.ServerThread;
import utils.Message;
import utils.MessageReader;
import utils.NetUtils;


public class ClientMain {
	
	public ServerSocket serverListener;
	public Socket socket;
	public Map<String, List> peers; 
	public Map<String, List> dialogList;
	public int port;
	private String hostAddress;
	private Message messageToServer;
	private Message messageFromServer;
	private BufferedReader in;
	private PrintWriter out;
	private MessageReader messageReader;
	public Boolean isAuthenticated;
	public ServerSocket peerServerSocket;
	private byte[] sessionKeyKas;
	
	
	public ClientMain(){
		
		try {

			initial();
			
	        //talking with peer(initiator)
	        
	        	new LocalListener();
	        	//connecting from peer(recipient)
	        	/*
	        	int port = 9000;
	        	while(NetUtils.isLocalPortUsing(port)){
	        		port++;
	        	}
	        	listenerPort = port;
	        	*/
	        	
	           //initiate login authentication with server
		        new ServerListener(this, this.socket);
		        //start thread deal with invitation
	            while(true){
	        		Socket peerSocket;
		        	peerSocket = peerServerSocket.accept();
					new PeerListener(this, peerSocket).start();
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
		
		isAuthenticated = false;
        System.out.println("Please input the port # of server:");
		BufferedReader inputRead = new BufferedReader(new InputStreamReader(System.in));
        String str = inputRead.readLine();
        int port = Integer.parseInt(str);
        System.out.println("Connecting server...");

		socket = new Socket(hostAddress, port);
    	
		System.out.println("Please assign a port number for client");
		String portClient = inputRead.readLine();
		int portOnClient = Integer.parseInt(portClient);
		
//		while(NetUtils.isLocalPortUsing(portClient)){
//			System.out.println("Port is used");
//			System.out.println("Please input again:");
//			portClient = is_reader.read();
//		}
//        is_reader.close();
        //暂时从控制台输入端口信息
        this.setPort(portOnClient);
    	peerServerSocket = new ServerSocket(portOnClient);

        System.out.println("peerServerSocket port number is "+ peerServerSocket.getLocalPort());
    	
	}

	public static void main(String args[]) throws IOException{

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
	
	public byte[] getSessionKeyKas() {
		return sessionKeyKas;
	}

	public void setSessionKeyKas(byte[] sessionKeyKas) {
		this.sessionKeyKas = sessionKeyKas;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}

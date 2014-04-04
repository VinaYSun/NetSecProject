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
	
	
	public ClientMain(){
		
		try {

			initialization();
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new PrintWriter(socket.getOutputStream(), true);
	         
			int protocolId = 1;
			int stepId = 3;
			messageToServer.setProtocolId(protocolId);
			messageToServer.setStepId(stepId);

			
			String clientInput;
			while((clientInput = inputReader.readLine())!=null){
				messageToServer.setData(clientInput.getBytes());
				String str = messageReader.messageToJson(messageToServer);
				out.println(str);
				
				String temp = new MessageReader().readInputStream(in);
				messageFromServer = messageReader.messageFromJson(temp);
		        String fromServer = new String(messageFromServer.getDataBytes(), "UTF-8");
				System.out.println("From server:" + fromServer);
				
			}
			
			inputReader.close();
//			out.close();
//			in.close();
//			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectException e){
			System.out.println("Cannot connect to server.");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void initialization() throws UnknownHostException, IOException, ConnectException {
		messageReader = new MessageReader();
		messageToServer = new Message();
		hostAddress = "127.0.0.1";
		port = 8899;
		
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

}

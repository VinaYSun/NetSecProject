package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.ChatRequest;
import server.ListRequest;
import server.LoginRequest;
import server.LogoutRequest;
import utils.Message;
import utils.MessageReader;

/**
 * listen to "CHAT" request from client
 * @author yaweisun
 *
 */
public class PeerListener extends Thread{
	
	private Socket peerSocket;
	private ClientMain clientMain;
	private BufferedReader in;
	private PrintWriter out;
	private Message messageFromPeer;
	private Message messageToPeer = null;
	private Boolean isConnected = false;
	private byte[] sessionKeyKbs = null;
	
	public PeerListener(ClientMain clientMain, Socket peerSocket) {
		System.out.println("实现监听 是否有人想我发出邀请");
		System.out.println("accept invitation, new client thread started");
		this.clientMain = clientMain;
		this.peerSocket = peerSocket;
		messageToPeer = new Message();
		messageFromPeer = new Message();
		sessionKeyKbs = clientMain.getSessionKeyKas();
	}

	@Override
	public void run() {
		try {
			handleConnectRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 1. unpack and read message from sender::  Kbs{Kab, A} Kab{n2}
	 * 2. send::  Kab{N2, g^bmodp}
	 * 3. receive message from client:: Kab{g^amodp}
	 */
	private void handleConnectRequest() {

		try {

			 in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
	         out = new PrintWriter(peerSocket.getOutputStream(), true);
	        
		     String tempIn = MessageReader.readInputStream(in);
		     messageFromPeer = MessageReader.messageFromJson(tempIn);
             System.out.println("Get peer msg: message(" + messageFromPeer.getProtocolId() + ", " + messageFromPeer.getStepId()+ ")");
             
             if(messageFromPeer.getProtocolId()==3 && messageFromPeer.getStepId() == 3){
	             
            	 //unpack and read message from sender::  Kbs{Kab, A} Kab{n2}
            	 
            	 //verify
            	 
            	 //send data
            	 
            	 messageToPeer.setData(" Kab{N2, g^bmodp}");
			     messageToPeer.setStepId(4);
	             messageToPeer.setProtocolId(3);
	
	             String str = MessageReader.messageToJson(messageToPeer);
			     System.out.println("Send peer msg: message(" + messageToPeer.getProtocolId() + ", " + messageToPeer.getStepId()+ ")");
	
	             //send message;
		         out.println(str);
		         
			     tempIn = MessageReader.readInputStream(in);
		         messageFromPeer = MessageReader.messageFromJson(tempIn);
		         System.out.println("Get peer msg: message(" + messageFromPeer.getProtocolId() + ", " + messageFromPeer.getStepId()+ ")");
		         
		         //
             }else{
	        	 messageToPeer.setData("Wrong message");
	             messageToPeer.setProtocolId(3);
			     messageToPeer.setStepId(7);
	         }
	         //when it gets the sessionkey
	         //break this loop
	         while(true){
		        	 
			 }
//	         socket.close(); 
	         
	    } catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	public Boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(Boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	
}

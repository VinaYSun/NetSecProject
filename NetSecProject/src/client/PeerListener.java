package client;

import java.net.Socket;

/**
 * listen to "CHAT" request from client
 * @author yaweisun
 *
 */
public class PeerListener extends Thread{
	
	private Socket peerSocket;
	private ClientMain clientMain;
	
	public PeerListener(ClientMain clientMain, Socket peerSocket) {
		System.out.println("实现监听 是否有人想我发出邀请");
		System.out.println("accept invitation, new client thread started");
		this.clientMain = clientMain;
		this.peerSocket = peerSocket;
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

		
	}
	
	
	
	
}

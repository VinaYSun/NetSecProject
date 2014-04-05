package client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import utils.Message;
import utils.MessageReader;

/**
 * communicate with server
 * @author yaweisun
 *
 */
public class ServerListener extends Thread{
	
	public ClientMain client;
	public Socket socket;
	private BufferedReader inputReader;
	private Message messageToServer;
	private Message messageFromServer;
	
	private BufferedReader in;
	private PrintWriter out;
	private byte[] nonceR1;
	private byte[] nonceR2;
	private byte[] nonceR3;
	
	public ServerListener(ClientMain client, Socket socket) {
		try{
		messageToServer = new Message();
		this.socket = socket;
		this.client = client;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
		nonceR1 = new byte[128];
		
		start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {

		// initial login authentication
		// 1. send "LOGIN"
		// 2. wait until receive {IP of local, Random R1}
		// 3. send Kpub{ga^modp, Random R1, Random R2, Username, W}
		// 4. wait until recieve Key{g^smodp, Random R3} key = Hash{R2|W}
		// 5. send Kas{R3}
		// update his state
		try {

			// 1. send "LOGIN"
			messageToServer = new Message(1, 1, "LOGIN".getBytes());
			String str = MessageReader.messageToJson(messageToServer);
			out.println(str);

			// 2. wait until receive {IP of local, Random R1}
			messageFromServer = MessageReader.getMessageFromStream(in);
			System.out.println("From server(reply message(1,1,Login)):\n "+ messageFromServer.getData());

			// convert String into InputStream
			InputStream is = new ByteArrayInputStream(messageFromServer.getDataBytes());

			// read it with BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String IpAddress = br.readLine();
			nonceR1 = br.readLine().getBytes();
			br.close();
			
			System.out.println("收到的address" + IpAddress);
			System.out.println("收到的nonce" + new String(nonceR1));
			
			// 3. send Kpub{g^amodp, Random R1, Random R2, Username, W}
			
			messageToServer = new Message(1, 3, "DHKey".getBytes());
			String str1 = MessageReader.messageToJson(messageToServer);
			out.println(str1);
			
			// 4. wait until recieve Key{g^smodp, Random R3} key = Hash{R2|W}
			messageFromServer = MessageReader.getMessageFromStream(in);
			System.out.println("From server(reply message(1,3)):\n "+ messageFromServer.getData());
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		handleSocket();
	}
	
	//send input message and get reply from server;
	// list logout request ticket
	private void handleSocket() {
		try {
			  
			String clientInput;

			while((clientInput = inputReader.readLine())!=null){
				
				int protocolId = 1;
				int stepId = 3;
				messageToServer.setProtocolId(protocolId);
				messageToServer.setStepId(stepId);
				messageToServer.setData(clientInput.getBytes());
				String str = MessageReader.messageToJson(messageToServer);
				out.println(str);
				
				messageFromServer = MessageReader.getMessageFromStream(in);
		        String fromServer = new String(messageFromServer.getDataBytes(), "UTF-8");
				System.out.println("From server:" + fromServer);
				
			}
			

		} catch (SocketTimeoutException e) {
			System.out.println("数据读取超时。");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

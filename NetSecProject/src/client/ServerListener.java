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
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import utils.CryptoUtils;
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
	private byte[] randomR1;
	//128-bits salt randomR2
	private String randomR2;
	private byte[] randomR3;
	private PublicKey publicKey;
	
	public ServerListener(ClientMain client, Socket socket) {
		try{
		messageToServer = new Message();
		this.socket = socket;
		this.client = client;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
		randomR1 = new byte[128];
		randomR2 = null;
		
		try {
			publicKey = CryptoUtils.getPublicKey("public.der");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
			messageToServer.setProtocolId(1);
			messageToServer.setStepId(1);
			messageToServer.setData("LOGIN");
			String str = MessageReader.messageToJson(messageToServer);
			out.println(str);

			
			// 2. wait until receive {IP of local, Random R1}
			messageFromServer = MessageReader.getMessageFromStream(in);
			
			if (messageFromServer.getProtocolId() == 1
				 && messageFromServer.getStepId() == 2) {

				System.out.println("From server(reply message(1,1,Login)):\n "
						+ messageFromServer.getData());

				// convert String into InputStream
				InputStream is = new ByteArrayInputStream(messageFromServer.getDataBytes());
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				String IpAddress = br.readLine();
				randomR1 = br.readLine().getBytes();
				is.close();
				br.close();

				System.out.println("收到的address" + IpAddress);
				System.out.println("收到的nonce" + new String(randomR1));

				// 3. send Kpub{g^amodp, Random R1, Random R2, Username, W}
				//create ciphertext
				String ciphertext = null;
				
				String DHkey = null;
				System.out.println("Please input your username and password: \n");
				System.out.println("Username:");
				//read username
				String username = "username";
				
				//read password
				System.out.println("Password:");
				String password = "password";
				
				randomR2 = CryptoUtils.getSalt();
				password = CryptoUtils.getSaltHash(password, randomR2);

				//				getList(DHkey, randomR1, username, password, randomR2);
				
				
				messageToServer.setProtocolId(1);
				messageToServer.setStepId(3);
				messageToServer.setData(ciphertext);
				String str3 = MessageReader.messageToJson(messageToServer);
				out.println(str3);
				
				
				// 4. wait until recieve Key{g^smodp, Random R3} key =
				// Hash{R2|W}
				messageFromServer = MessageReader.getMessageFromStream(in);
				System.out.println("From server(reply message(1,3)):\n "
						+ messageFromServer.getData());
			}
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
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
				System.out.println("server socket port:" + socket.getPort());
				System.out.println("local socket port"+ socket.getLocalPort());
     	        System.out.println("Channel number"+socket.getChannel());

			}
			

		} catch (SocketTimeoutException e) {
			System.out.println("数据读取超时。");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws Exception{
    	String file1 = "public.der";
    	String file2 = "private.der";
    	CryptoUtils.getPublicKey(file1);
    	
    	CryptoUtils.getPrivateKey(file2);


    }
}

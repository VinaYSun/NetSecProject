package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import utils.Message;
import utils.MessageReader;

/**
 * send invitation to the other client
 * @author yaweisun
 *
 */
public class LocalListener extends Thread{
	
	public Socket socket;
	
	public LocalListener(){
		
	}

	public LocalListener(ClientMain client, String chatToName) throws UnknownHostException, IOException {
		String hostAddress = "127.0.0.1";
		//通过chatToName 查找到对应的IP/Port number, 
		int port = 9999;
		socket = new Socket(hostAddress, port);
	}

	@Override
	public void run() {
		try {
			
			while(true){
				
				String message = this.readInput();
				if(message != null)
				sendRequest();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void sendRequest() throws IOException {
		
		
		
	}

	/**
	 * read all userinput onetime
	 * @return input
	 * @throws IOException 
	 */
	private String readInput() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		input = reader.readLine();
		System.out.println(input);
		return input;
	}
}

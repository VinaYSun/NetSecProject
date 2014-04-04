package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import utils.Message;
import utils.MessageReader;

/**
 * communicate with message recipient peers
 * @author yaweisun
 *
 */
public class LocalListener extends Thread{
	
	public Socket socket;
	
	public LocalListener(){
		
	}
	
	public LocalListener(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			while(true){
				String message = this.readInput();
				if(message != null)
					sendRequest(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void sendRequest(String str) throws IOException {
		
		 try{
			 
			 System.out.println("user input is "+ str);
			 if(socket.isClosed()){
				 System.out.println();
				 socket = new Socket("127.0.0.1",8899);
			 }
			 Writer writer = new OutputStreamWriter(this.socket.getOutputStream());  
			 System.out.println("pass");
			 byte[] b = str.getBytes(Charset.forName("UTF-8"));

		     Message msg = new Message(1,1,b);
			 System.out.println("pass");

		     String msgbyString = new MessageReader().messageToJson(msg);
		     writer.write(msgbyString); 
		     writer.write("eof");  
		     writer.flush(); 
		     writer.close();
		     System.out.println("I sent "+ msgbyString +"to server");		

		 }catch(IOException e){
			 e.printStackTrace();
		 }
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

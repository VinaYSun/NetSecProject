package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;

import utils.Message;

public class ServerChat {
	
	
	public static void main(String args[]){

		int port = 8899;  
		 
		 try{
		 
		 //open socket at port 8899
		 ServerSocket server = new ServerSocket(port);  	
		 while (true) {  
			 
			 //for each attempting socket, initiate a new thread to process
	         Socket socket = server.accept();  
	         new Thread(new Task(socket)).start();  
	      }  
		 }catch(IOException e){
			 
		 }
	}
	
	
	 /**
	  * For each socket connection from client, open up a thread to handle this socket
	  */
	 static class Task implements Runnable{
		
		private Socket socket; 
		public Task(Socket socket){
			this.socket = socket;	
		}
		
		@Override
		public void run() {
			try {
				handleSocket();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		/**
		 * 1.get message from client
		 * 2.send reply message
		 */
		private void handleSocket() {
			
			try {
				
				DataInputStream is = new DataInputStream(socket.getInputStream());
				
				String tempJson;
//				tempJson = br.readLine();
				
//				while((tempJson=br.readLine()) != null){
//					System.out.println(tempJson);  
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}

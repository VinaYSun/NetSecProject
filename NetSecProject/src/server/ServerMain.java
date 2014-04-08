package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import utils.Message;
import utils.NetUtils;

public class ServerMain {
	
	public Map<String, String> passwordBook = null;
	public ServerSocket serverSocket;
	public HashMap<String, byte[]> userList = null;
	public HashMap<String, byte[]> userAddress = null;

	public ServerMain() throws IOException {
		try {
			initialize();
			NetUtils.isLocalPortUsing(8899);
			serverSocket = new ServerSocket(8899);
			while (true) {
				Socket client;
				client = serverSocket.accept();
				new ServerThread(this, client).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}
	}
	
	private void initialize() throws FileNotFoundException {
		userList = new HashMap<String, byte[]>();
		passwordBook = new HashMap<String, String>();
		userAddress = new HashMap<String, byte[]>();
		loadPassword("password.txt");
	}

	private void loadPassword(String passwordfile) throws FileNotFoundException  {
		FileReader reader = new FileReader(passwordfile);
        BufferedReader br = new BufferedReader(reader);
        String s1 = null;
        try {
			while((s1 = br.readLine()) != null) {
				String[] section = s1.split(":");
			    String name = section[0];
			    String pwdsalt = section[1];
			    String pwd = section[2];
			    System.out.println(name);
			    System.out.println(pwdsalt);
			    System.out.println(pwd);
			    passwordBook.put(name, pwdsalt+":"+pwd);
			}
			reader.close();
			br.close();
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String args[]){

		 try {
			new ServerMain();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
	
	public Map<String, String> getPasswordBook() {
		return passwordBook;
	}

	public void setPasswordBook(Map<String, String> passwordBook) {
		this.passwordBook = passwordBook;
	}

	public ServerSocket getServerSocket() {
			return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
	}
	
	public HashMap<String, byte[]> getUserList() {
		return userList;
	}

	public void setUserList(HashMap<String, byte[]> userList) {
		this.userList = userList;
	}
	

	public HashMap<String, byte[]> getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(HashMap<String, byte[]> userAddress) {
		this.userAddress = userAddress;
	}
}

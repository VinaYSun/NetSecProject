package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerListener extends Thread{
	
	public Socket socket;
	public ServerListener(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run(){
		 handleSocket();
	}

	private void handleSocket() {
		try {
			 if(socket.isClosed()){
				 socket = new Socket("127.0.0.1",8899);
			 }
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 设置超时间为10秒
			socket.setSoTimeout(10 * 1000);
			StringBuffer sb = new StringBuffer();
			String temp;
			int index;
			while ((temp = br.readLine()) != null) {
				if ((index = temp.indexOf("eof")) != -1) {
					sb.append(temp.substring(0, index));
					break;
				}
				sb.append(temp);
			}

			System.out.println("from server: " + sb);
			br.close();
//			socket.close();

		} catch (SocketTimeoutException e) {
			System.out.println("数据读取超时。");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

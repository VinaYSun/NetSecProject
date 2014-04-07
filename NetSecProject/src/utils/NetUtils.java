package utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetUtils {  
    
    /*** 
     * true:already in using  false:not using  
     * @param port 
     */  
    public static boolean isLocalPortUsing(int port){  
        boolean flag = true;  
        try {  
            flag = isPortUsing("127.0.0.1", port);  
        } catch (Exception e) {  
        }  
        return flag;  
    }  
    
    /*** 
     *  true:already in using  false:not using  
     * @param host 
     * @param port 
     * @throws UnknownHostException  
     */  
    public static boolean isPortUsing(String host,int port) throws UnknownHostException{  
        boolean flag = false;  
        InetAddress theAddress = InetAddress.getByName(host);  
        try {  
            Socket socket = new Socket(theAddress,port);  
            flag = true;  
            System.out.println("this port is using");
        } catch (IOException e) {  
              
        }  
        return flag;  
    }  
}  
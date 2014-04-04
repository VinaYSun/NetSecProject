package utils;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MessageReader {
	
	public MessageReader(){
		
	}
	
	/**
	 * convert json to message
	 * @param inputstring
	 * @return message
	 */
	public Message messageFromJson(String inputstring) {
		Message msg = new Message();
		msg = null;
		Gson gson = new Gson();
		msg = gson.fromJson(inputstring, new TypeToken<Message>() {}.getType());
//		System.out.println("message is converted from json");
		return msg;
	}
	
	/**
	 * convert message to json
	 * add "eof" as finishing symbol
	 * @param message
	 * @return string
	 */
	public String messageToJson(Message msg) {
		 String str = null;
		 Gson gson = new Gson();
		 str = gson.toJson(msg, new TypeToken<Message>() {}.getType());
		 str = str + "eof";
		 return str;
	}
	
	/**
	 * read data from inputStream
	 * @param socket
	 * @return inputstream in String
	 * @throws IOException
	 */
	public String readInputStream(BufferedReader br) throws IOException{
        StringBuilder sb = new StringBuilder();  
        String temp;  
        int index;  
        while ((temp=br.readLine()) != null) {  
  		  //read inputstream until meat end symbol "eof"
           if ((index = temp.indexOf("eof")) != -1) {
            sb.append(temp.substring(0, index));  
               break;  
           }  
           sb.append(temp);  
        }
//        System.out.println("INPUT STREAM is (without eof) "+ sb);  
        return sb.toString();
	}
	
	public Message getMessageFromStream(BufferedReader br) throws IOException{
		String str;
		str = this.readInputStream(br);
		Message msg = new Message();
		msg = this.messageFromJson(str);
	    return msg;
	}
}

package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MessageConverter {
	
	public MessageConverter(){
		
	}
	
	/**
	 * convert json to message
	 * @param inputstring
	 * @return message
	 */
	public Message messageFromJson(String inputstring) {
		Message msg = new Message();
		Gson gson = new Gson();
		msg = gson.fromJson(inputstring, new TypeToken<Message>() {}.getType());
		return msg;
	}
	
	/**
	 * convert message to json
	 * @param message
	 * @return string
	 */
	public String messageToJson(Message msg) {
		 String str = null;
		 Gson gson = new Gson();
		 str = gson.toJson(msg, new TypeToken<Message>() {}.getType());
		 return str;
	}
	
}

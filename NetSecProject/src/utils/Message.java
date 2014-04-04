package utils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;

/**
 * constructs message 
 * @author yaweisun
 * @param  protocolId
 * @param  stepId
 * @param  data
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int protocolId;
	private int stepId;
	private byte[] data;
	private String timestamp;
	
	public Message(int protocolId, int stepId, byte[] data){
		this.protocolId = protocolId;
		this.stepId = stepId;
		this.data = data;
		this.timestamp = this.getTimestampToString(System.currentTimeMillis());
	}
	
	public Message() {
		this.data = null;
		this.timestamp = this.getTimestampToString(System.currentTimeMillis());
		this.protocolId = 0;
		this.stepId = 0;
	}
	

	public int getProtocolId() {
		System.out.println("is protocolid there? ");
		return protocolId;
	}

	public void setProtocolId(int protocolId) {
		this.protocolId = protocolId;
	}

	public int getStepId() {
		return stepId;
	}

	public void setStepId(int stepId) {
		this.stepId = stepId;
	}

	public byte[] getDataBytes() {
		return data;
	}
	
	public String getData() throws UnsupportedEncodingException {
		return new String(data, "UTF-8");
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}

	public void setData(String data){
		this.data = data.getBytes();
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestampToString(long timestamp){
		  SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		  Long time=new Long(timestamp);
		  String d = format.format(time);
		  return d;
	}
}

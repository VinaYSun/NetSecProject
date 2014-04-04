package utils;

import java.io.Serializable;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
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

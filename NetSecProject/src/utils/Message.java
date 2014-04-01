package utils;

import java.io.Serializable;

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
	
	
	public Message(int protocolId, int stepId, byte[] data){
		this.protocolId = protocolId;
		this.stepId = stepId;
		this.data = data;
	}
	
	public int getProtocolId() {
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

	
}

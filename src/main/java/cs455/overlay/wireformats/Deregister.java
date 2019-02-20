package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Deregister implements Event{
	private EventType type;
	public boolean IPverified = false;
	byte[] deregisteringIp;
	public byte[] getDeregisteringIp() {
		return deregisteringIp;
	}
	int deregisteringPort;
	public int getDeregisteringPort() {
		return deregisteringPort;
	}

	byte isSuccessful = 1;
	public byte getIsSuccessful() {
		return isSuccessful;
	}
	String additionalInfo="";
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	
 	public Deregister(byte[] registeringIp, int registeringPort) {
 		this.type = EventType.DEREGISTER_REQUEST;
		this.deregisteringIp=registeringIp;
		this.deregisteringPort = registeringPort;
	}
 	public Deregister(boolean isSuccessful, String additionalInfo) {
 		this.type = EventType.DEREGISTER_RESPONSE;
 		
 		if (isSuccessful) this.isSuccessful = 1;
 		else this.isSuccessful = 0;
 		
 		this.additionalInfo = additionalInfo;
 	}
 	public Deregister(boolean isSuccessful) {
 		this(isSuccessful,"");
 	}
	
 	@Override
	public EventType getType() {
		return type;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			bos.write(getType().ordinal());
			if(this.getType()==EventType.DEREGISTER_REQUEST) {
				bos.write(deregisteringIp);
				ByteEncoder.writeEncodedInt(deregisteringPort, bos);
			}
			else {
				bos.write(isSuccessful);
				ByteEncoder.writeEncodedString(additionalInfo, bos);
			}
			bos.flush();
			encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	public Deregister(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);		
		this.type = EventType.values()[bis.read()];
		if (type==EventType.DEREGISTER_REQUEST) {			
			this.deregisteringIp = new byte[4];
			bis.read(this.deregisteringIp, 0, 4);
			this.deregisteringPort=ByteEncoder.readEncodedInt(bis);
		}
		else {
			this.isSuccessful = (byte)bis.read();
			this.additionalInfo = ByteEncoder.readEncodedString(bis);
		} 
	}
	
	public String toString() {
		String result = "";
		result += "Message Type: "+this.getType()+"\t";
		if(type==EventType.DEREGISTER_REQUEST) {		
			try {
				result += "Registering IP: "+InetAddress.getByAddress(this.deregisteringIp).getHostAddress()+"\t";
			} catch (UnknownHostException e) {
				result += "Registering IP: "+"N\\A"+"\t";
			}
			result += "Registering Port: "+this.deregisteringPort;
		}
		else {
			result += "Status Code: "+this.isSuccessful+"\t";
			result += "Additional Info: "+this.additionalInfo;
		}
		return result;
		
	}
}

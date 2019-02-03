package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Register implements Event{
	private EventType type;
	
	byte[] registeringIp;
	public byte[] getRegisteringIp() {
		return registeringIp;
	}
	int registeringPort;
	public int getRegisteringPort() {
		return registeringPort;
	}

	byte isSuccessful = 1;
	String additionalInfo="";
	
 	public Register(byte[] registeringIp, int registeringPort) {
 		this.type = EventType.REGISTER_REQUEST;
		this.registeringIp=registeringIp;
		this.registeringPort = registeringPort;
	}
 	public Register(boolean isSuccessful, String additionalInfo) {
 		this.type = EventType.REGISTER_RESPONSE;
 		
 		if (isSuccessful) this.isSuccessful = 1;
 		else this.isSuccessful = 0;
 		
 		this.additionalInfo = additionalInfo;
 	}
 	public Register(boolean isSuccessful) {
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
			if(this.getType()==EventType.REGISTER_REQUEST) {
				bos.write(registeringIp);
				bos.write(registeringPort);
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
	public Register(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);		
		this.type = EventType.values()[bis.read()];
		if (type==EventType.REGISTER_REQUEST) {			
			this.registeringIp = new byte[4];
			bis.read(this.registeringIp, 0, 4);
			this.registeringPort=bis.read();
		}
		else {
			this.isSuccessful = (byte)bis.read();
			this.additionalInfo = ByteEncoder.readEncodedString(bis);
		} 
	}
	
	public String toString() {
		String result = "";
		result += "Message Type: "+this.getType()+"\t";
		if(type==EventType.REGISTER_REQUEST) {		
			try {
				result += "Registering IP: "+InetAddress.getByAddress(this.registeringIp).getHostAddress()+"\t";
			} catch (UnknownHostException e) {
				result += "Registering IP: "+"N\\A"+"\t";
			}
			result += "Registering Port: "+this.registeringPort;
		}
		else {
			result += "Status Code: "+this.isSuccessful+"\t";
			result += "Additional Info: "+this.additionalInfo;
		}
		return result;
		
	}
}

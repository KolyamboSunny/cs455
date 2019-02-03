package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Register implements Event{

	byte[] registeringIp;
	public byte[] getRegisteringIp() {
		return registeringIp;
	}
	int registeringPort;
	public int getRegisteringPort() {
		return registeringPort;
	}

	public Register(byte[] registeringIp, int registeringPort) {
		this.registeringIp=registeringIp;
		this.registeringPort = registeringPort;
	}
	
	@Override
	public EventType getType() {
		return EventType.REGISTER;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			bos.write(getType().ordinal());
			bos.write(registeringIp);
			bos.write(registeringPort);
			bos.flush();
			encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	public Register(byte[] byteEncoding) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);		
		try {		  
		  EventType type = EventType.values()[bis.read()];
		  if (type!=this.getType())
			  throw new Exception("Encode message has an unexpected type");
		  this.registeringIp = new byte[4];
		  bis.read(this.registeringIp, 0, 4);
		  this.registeringPort=bis.read();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public String toString() {
		String result = "";
		result += "Message Type: "+this.getType()+"\t";
		try {
			result += "Registering IP: "+InetAddress.getByAddress(this.registeringIp).getHostAddress()+"\t";
		} catch (UnknownHostException e) {
			result += "Registering IP: "+"N\\A"+"\t";
		}
		result += "Registering Port: "+this.registeringPort;
		return result;
		
	}
}

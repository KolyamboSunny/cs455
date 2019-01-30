package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class Register implements Event{

	String registeringIp;
	int registeringPort;
	
	public Register(String registeringIp, int registeringPort) {
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
		ObjectOutput out = null;
		
		try {
		  out = new ObjectOutputStream(bos); 
		  
		  out.writeObject(this.getType());
		  
		  out.writeObject(registeringIp);		 		  
		  out.writeInt(registeringPort);
		  		  
		  out.flush();
		  encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	public Register(byte[] byteEncoding) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  
		  EventType type = (EventType)in.readObject();
		  if (type!=this.getType())
			  throw new Exception("Encode message has an unexpected type");
		  
		  this.registeringIp=(String)in.readObject();
		  this.registeringPort=in.readInt();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}

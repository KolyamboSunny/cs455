package cs455.overlay.wireformats;

import java.io.*;

public class Message implements Event{

	public String dest;
	public String src;	
	public long payload;	
	
	public Message(String dest, String src, long payload) {
		this.dest = dest;
		this.src=src;
		this.payload = payload;
	}	
	
	public String toString() {
		String result = "";
		result += "Source: "+this.src+"\t";
		result += "Destination: "+this.dest+"\t";
		result += "Payload: "+this.payload;
		return result;
		
	}
	
	@Override
	public EventType getType() {
		return EventType.MESSAGE;		
	}

	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		
		try {
		  out = new ObjectOutputStream(bos); 
		  
		  out.writeObject(this.getType());
		  
		  out.writeObject(dest);		 		  
		  out.writeObject(src);
		  
		  out.writeLong(payload);
		  out.flush();
		  encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	
	public Message(byte[] byteEncoding) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  
		  EventType type = (EventType)in.readObject();
		  if (type!=EventType.MESSAGE)
			  throw new Exception("Encode message has an unexpected type");
		  
		  this.dest=(String)in.readObject();
		  this.src=(String)in.readObject();

		  this.payload = in.readLong();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public static void main(String[] args) {
		Message msg = new Message("zhopa","denver",1488);
		byte[] encoded = msg.getBytes();
		
		Message msg2;
		try {
			msg2 = new Message(encoded);
			System.out.println(msg2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

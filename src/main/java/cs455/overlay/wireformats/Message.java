package cs455.overlay.wireformats;

import java.io.*;
import java.net.InetSocketAddress;

public class Message implements Event{

	private InetSocketAddress dest;
	public InetSocketAddress getDestination() {
		return dest;
	}
	private int payload;	
	public int getPayload() {
		return payload;
	}
	
	public Message(InetSocketAddress dest, int payload) {
		this.dest = dest;
		this.payload = payload;
	}	

	@Override
	public EventType getType() {
		return EventType.MESSAGE;		
	}

	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			bos.write(getType().ordinal());
			ByteEncoder.writeEncodedAddress(dest, bos);
			ByteEncoder.writeEncodedInt(payload, bos);
			
			bos.flush();
			encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	

	public Message(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);
		
		@SuppressWarnings("unused")
		EventType type = EventType.values()[bis.read()];
		
		this.dest = ByteEncoder.readEncodedAddress(bis);
		this.payload = ByteEncoder.readEncodedInt(bis);		 
	}
	
	public String toString() {
		String result = "";		
		result += "Destination: "+this.dest+"\t";
		result += "Payload: "+this.payload;
		return result;
		
	}
}

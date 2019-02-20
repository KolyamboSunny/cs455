package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TrafficSummaryRequest implements Event{
 	public TrafficSummaryRequest() { 	
	}
 	
 	@Override
	public EventType getType() {
		return EventType.PULL_TRAFFIC_SUMMARY;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			bos.write(getType().ordinal());

			bos.flush();
			encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	public TrafficSummaryRequest(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);
		
		@SuppressWarnings("unused")
		EventType type = EventType.values()[bis.read()];
			 
	}
	
	public String toString() {
		String result = "";
		result += "Message Type: "+this.getType();
		
		return result;		
	}
}

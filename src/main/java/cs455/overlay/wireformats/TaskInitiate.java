package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TaskInitiate implements Event{
	int numberOfRounds;
	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	
 	public TaskInitiate(int numberOfRounds) {
 		this.numberOfRounds = numberOfRounds;
	}
 	
 	@Override
	public EventType getType() {
		return EventType.TASK_INITIATE;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			bos.write(getType().ordinal());

			ByteEncoder.writeEncodedInt(numberOfRounds, bos);
			
			bos.flush();
			encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return encodedEvent;
	}
	public TaskInitiate(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);
		
		@SuppressWarnings("unused")
		EventType type = EventType.values()[bis.read()];
		
		this.numberOfRounds = ByteEncoder.readEncodedInt(bis);		 
	}
	
	public String toString() {
		String result = "";
		result += "Message Type: "+this.getType()+"\t";
		result += "Rounds: "+this.getNumberOfRounds();
		
		return result;		
	}
}

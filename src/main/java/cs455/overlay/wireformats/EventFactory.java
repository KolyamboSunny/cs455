package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EventFactory {
	
	private static EventType getEventType(byte[] encodedEvent) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(encodedEvent);
		  
		EventType type = EventType.values()[bis.read()];;
		return type;		
	}
	
	public static Event getEvent(byte[] encodedEvent) throws Exception {
		EventType encodedEventType= getEventType(encodedEvent); 		
		switch(encodedEventType) {
			case MESSAGE:
				return new Message(encodedEvent);
			case REGISTER:
				return new Register(encodedEvent);
			default:
				throw new Exception("Message type unknown");
		}		
	}
}

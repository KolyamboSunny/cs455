package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

public class EventFactory {
	
	private static EventType getEventType(byte[] encodedEvent) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(encodedEvent);
		int eventTypeIndex = bis.read();
		EventType type = null;
		
		try {
			
			type = EventType.values()[eventTypeIndex];
		}catch(Exception e) {
			System.err.println("Found array element out of bound: "+eventTypeIndex);
			System.err.println(Arrays.toString(encodedEvent));
		}
		bis.close();
		return type;		
	}
	
	public static Event getEvent(byte[] encodedEvent) throws Exception {
		EventType encodedEventType= getEventType(encodedEvent); 		
		switch(encodedEventType) {
			case MESSAGE:
				return new Message(encodedEvent);
			case REGISTER_REQUEST:
				return new Register(encodedEvent);
			case REGISTER_RESPONSE:
				return new Register(encodedEvent);
			case MESSAGING_NODES_LIST:
				return new MessagingNodesList(encodedEvent);
			case LINK_WEIGHTS:
				return new LinkWeights(encodedEvent);
			case TASK_INITIATE:
				return new TaskInitiate(encodedEvent);
			case TASK_COMPLETE:
				return new TaskComplete(encodedEvent);
			case PULL_TRAFFIC_SUMMARY:
				return new TrafficSummaryRequest(encodedEvent);
			case TRAFFIC_SUMMARY:
				return new TrafficSummaryResponse(encodedEvent);
			default:
				throw new Exception("Message type unknown");
		}		
	}
}

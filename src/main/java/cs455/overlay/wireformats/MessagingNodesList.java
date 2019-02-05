package cs455.overlay.wireformats;

public class MessagingNodesList implements Event {

	public MessagingNodesList(byte[] encoded) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public EventType getType() {		
		return EventType.MESSAGING_NODES_LIST;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}

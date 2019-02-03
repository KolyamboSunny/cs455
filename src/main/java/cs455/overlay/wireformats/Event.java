package cs455.overlay.wireformats;

public interface Event {	
	public EventType getType();	
	public byte[] getBytes();
}

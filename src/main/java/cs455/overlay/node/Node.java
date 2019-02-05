package cs455.overlay.node;

import java.net.InetSocketAddress;
import cs455.overlay.wireformats.Event;

public interface Node {

	public void onEvent(Event event) throws Exception;
	public void addContact(InetSocketAddress address);
}

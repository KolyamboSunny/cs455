package cs455.overlay.node;

import java.net.InetAddress;
import java.net.SocketAddress;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;

public interface Node {

	public void onEvent(Event event);
	
	public void addContactsEntry(SocketAddress address,TCPSender sender);
}

package cs455.overlay.node;

import java.io.IOException;
import java.net.SocketAddress;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

public class Registry implements Node {
	
	TCPServerThread serverThread = null;
	
	public Registry(int registryPort) throws IOException {		

		serverThread = new TCPServerThread(this);
		Thread sthread = new Thread(serverThread);
		sthread.start();							
	}
	
	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addContactsEntry(SocketAddress address, TCPSender sender) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		System.out.println(args[0]);

	}

}

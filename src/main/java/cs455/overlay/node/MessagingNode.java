package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node{
	TCPServerThread serverThread = null;
	TCPSender sender = null;
	HashMap<SocketAddress, TCPSender> contacts = new HashMap<SocketAddress, TCPSender>();
	
	public MessagingNode() throws IOException {		

		serverThread = new TCPServerThread(this);
		Thread sthread = new Thread(serverThread);
		sthread.start();							
	}
	
	public void register(String registryHost, int registryPort) {
		
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		switch (event.getType()) {
		case MESSAGE: 
			onMessageRecieved((Message)event);
			break;
		default:
			throw new Exception("Event of this type is not supported");
			//break;
		}
		
	}	
	private void onMessageRecieved(Message recievedMessage) {
		System.out.println(recievedMessage);
	}
	
	public void sendMessage(SocketAddress dest, long payload) {
		Message msg = new Message(serverThread.getAddress().toString(),dest.toString(),payload);
		Socket socket = new Socket();
		try {
			socket.connect(dest);
			TCPSender sender = new TCPSender(socket);
			sender.sendData(msg.getBytes());
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//sender.sendData(dataToSend);
	}
	
	
	public SocketAddress getServerAddress() {
		return serverThread.getAddress();
	}
	public void addContactsEntry(SocketAddress address,TCPSender sender) {
		contacts.put(address, sender);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}


}

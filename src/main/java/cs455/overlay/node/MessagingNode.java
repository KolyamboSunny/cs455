package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node{
	TCPServerThread serverThread = null;
	TCPSender registrySender = null;
	HashMap<InetSocketAddress, TCPSender> contacts = new HashMap<InetSocketAddress, TCPSender>();
	
	public MessagingNode(String registryHost, int registryPort) throws IOException {						
		serverThread = new TCPServerThread(this);
		Thread sthread = new Thread(serverThread);
		sthread.start();
		
		register(registryHost, registryPort);
	}
	
	public void register(String registryHost, int registryPort) throws UnknownHostException, IOException {
		this.registrySender = new TCPSender(registryHost,registryPort);
		String selfHost = this.serverThread.getAddress().getCanonicalHostName();
		int selfPort = this.serverThread.getPort();
		registrySender.sendData(new Register(selfHost,selfPort).getBytes());
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
	
	public void sendMessage(InetSocketAddress dest, long payload) {
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String registryHost = args[0];
		int registryPort = Integer.parseInt(args[1]);
		try {
			@SuppressWarnings("unused")
			MessagingNode m = new MessagingNode(registryHost,registryPort);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}

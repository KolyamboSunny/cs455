package cs455.overlay.node;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.transport.*;
import cs455.overlay.util.OverlayGraph;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node{
	TCPServerThread serverThread = null;
	TCPSender registrySender = null;
	
	Map<InetSocketAddress, TCPSender> contacts = new HashMap<InetSocketAddress, TCPSender>();
	Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights;
	
	OverlayGraph overlayGraph;
	RoutingCache routingHandler;
	
	InetSocketAddress ownAddress;
	public MessagingNode(String registryHost, int registryPort) throws IOException {						
		serverThread = new TCPServerThread(this);
		Thread sthread = new Thread(serverThread);
		sthread.start();
		
		ownAddress = NodeUtilHelpers.constructAddress(this.serverThread.getAddress().getAddress(), this.serverThread.getPort());
		
		register(registryHost, registryPort);
	}
	
	private void registerWithOtherNode(InetSocketAddress node) throws UnknownHostException, IOException {
		TCPSender sender = contacts.get(node);
		sender.sendData(new Register(ownAddress.getAddress().getAddress(),ownAddress.getPort()).getBytes());
	}
	public void register(String registryHost, int registryPort) throws UnknownHostException, IOException {
		this.registrySender = new TCPSender(registryHost,registryPort);
		byte[] selfHost = this.serverThread.getAddress().getAddress();
		int selfPort = this.serverThread.getPort();
		this.registrySender.sendData(new Register(selfHost,selfPort).getBytes());
	}
		
	@Override
	public void onEvent(Event event) throws Exception {
		switch (event.getType()) {
		case MESSAGE: 
			onMessageRecieved((Message)event);
			break;		
		case REGISTER_RESPONSE: 
			onRegisterResponseRecieved((Register)event);
			break;
		case REGISTER_REQUEST:
			//possible if one Messaging node is establishing a duplex connection with the other
			onRegisterRequestRecieved((Register)event);
			break;
		case MESSAGING_NODES_LIST: 
			onMessagingNodesListRecieved((MessagingNodesList)event);
			break;
		case LINK_WEIGHTS:
			onLinkWeightsRecieved((LinkWeights)event);
			break;
		default:
			throw new Exception("Event of this type is not supported");
			//break;
		}
		
	}	

	private void onMessageRecieved(Message recievedMessage) {
		System.out.println(recievedMessage);
	}
	private void onRegisterResponseRecieved(Register recievedRegisterResponse) {
		System.out.println(recievedRegisterResponse);
	}
	private void onMessagingNodesListRecieved(MessagingNodesList recievedMessagingNodesList) {				
		//establish connections to the desired nodes
		boolean success=true;
		for(InetSocketAddress node : recievedMessagingNodesList.destinations) {
			try {
				contacts.put(node, new TCPSender(node));	
				registerWithOtherNode(node);
			} catch (IOException e) {
				success = false;				
				System.err.println("Failed to establish connection to "+node.toString());
				if(contacts.containsKey(node))
					contacts.remove(node);
			}
		}
		if(success)System.out.println("All connections are established. Number of connections: "+contacts.size());
	}
	private void onRegisterRequestRecieved(Register registrationRequest) {
		System.out.println(registrationRequest);
		InetSocketAddress address = NodeUtilHelpers.constructAddress(registrationRequest.getRegisteringIp(),registrationRequest.getRegisteringPort());
		// check if the node has been previously registered
		if (contacts.containsKey(address)) {
			contacts.get(address).sendData(new Register(false,"This node has already been registered").getBytes());
			return;
		}
		if (!registrationRequest.IPverified) {
			contacts.get(address).sendData(new Register(false,"Your IP mismatches the one in registration request").getBytes());
			return;
		}
		
		try {
			contacts.put(address, new TCPSender(address));
			contacts.get(address).sendData(new Register(true).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			contacts.get(address).sendData(new Register(false,e.getMessage()).getBytes());
		}
	}
	private void onLinkWeightsRecieved(LinkWeights recievedLinkWeights) {
		//System.out.println(recievedLinkWeights);
		this.linkWeights = recievedLinkWeights.getLinkWeights();
		try {
			this.overlayGraph = new OverlayGraph(linkWeights);
			routingHandler = new RoutingCache(overlayGraph, ownAddress);
			System.out.println("Link weights are recieved and processed. Ready to send messages.");
		} catch (Exception e) {			// 
			System.err.println("Could not contruct overlay graph with the retirieved weights");
		}
	}
	
	public void sendMessage(InetSocketAddress dest, long payload) throws UnknownHostException {
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

package cs455.overlay.node;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.transport.*;
import cs455.overlay.util.OverlayGraph;
import cs455.overlay.wireformats.*;

public class MessagingNode implements Node{
	TCPServerThread serverThread = null;
	TCPSender registrySender = null;
	
	Map<InetSocketAddress, TCPSender> contacts = new HashMap<InetSocketAddress, TCPSender>();
	Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights;

	
	public OverlayGraph overlayGraph;
	RoutingCache routingHandler;
	
	int recieveTracker = 0;
	int sendTracker = 0;
	int relayTracker = 0;
	
	long recieveSummation = 0;
	long sendSummation = 0;
	
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
	
	public void deregister() throws UnknownHostException, IOException {
		byte[] selfHost = this.serverThread.getAddress().getAddress();
		int selfPort = this.serverThread.getPort();
		this.registrySender.sendData(new Deregister(selfHost,selfPort).getBytes());
	}
		
	@Override
	public void onEvent(Event event) throws Exception {
		switch (event.getType()) {		
		case REGISTER_RESPONSE: 
			onRegisterResponseRecieved((Register)event);
			break;
		case DEREGISTER_RESPONSE: 
			onDeregisterResponseRecieved((Deregister)event);
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
		case TASK_INITIATE:
			onTaskInitiateRecieved((TaskInitiate)event);
			break;
		case MESSAGE:
			onMessageRecieved((Message)event);
			break;
		case PULL_TRAFFIC_SUMMARY:
			onTrafficSummaryRequest((TrafficSummaryRequest)event);
			break;		
		default:
			throw new Exception("Event of this type is not supported");
			//break;
		}
		
	}	

	
	private void onRegisterResponseRecieved(Register recievedRegisterResponse) {
		//System.out.println(recievedRegisterResponse);
	}
	private void onDeregisterResponseRecieved(Deregister recievedDeregisterResponse) {
		this.registrySender.closeConnection();
		System.err.println(recievedDeregisterResponse);
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
		//System.out.println(registrationRequest);
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
	private void onTaskInitiateRecieved(TaskInitiate orderToStart) {
		//System.out.println(orderToStart);
		
		int numberOfRounds = orderToStart.getNumberOfRounds();
		for(int round =0;round<numberOfRounds;round++) {
			//randomly select other node to send message to
			try {
				InetSocketAddress dest = overlayGraph.getRandomNode(this.ownAddress);
				sendMessage(dest);

			} catch (Exception e) {
				System.err.println(e);
			}		
		}
		
		//inform registry that the task is done
		TaskComplete report = new TaskComplete(this.ownAddress.getAddress().getAddress(),this.ownAddress.getPort());
		registrySender.sendData(report.getBytes());
	}
	private void onMessageRecieved(Message recievedMessage) {
		//System.out.println("Recieved: "+recievedMessage);
		InetSocketAddress dest = recievedMessage.getDestination();
		if (dest.equals(ownAddress)) {
			int payload = recievedMessage.getPayload();
			this.recieveTracker++;
			this.recieveSummation+=payload;
			
				
			
		}
		else {
			this.sendMessage(recievedMessage);
			this.relayTracker++;
		}
	}
	public List<List<InetSocketAddress>> getRoutes() throws Exception{
		if (overlayGraph==null)
			throw new Exception("Link weights were not yet acquired.");
		List<List<InetSocketAddress>> result = new ArrayList<List<InetSocketAddress>>();
		for(InetSocketAddress destination:overlayGraph.getNodes()) {
			result.add(routingHandler.computeShortestRoute(destination));
		}
			
		return result;
	}
	public int sendMessage(InetSocketAddress dest) throws UnknownHostException {
		int payload = new Random().nextInt();
		this.sendMessage(dest, payload);
		return payload;
	}
	public void sendMessage(InetSocketAddress dest, int payload) throws UnknownHostException {
		Message msg = new Message(dest,payload);
		this.sendTracker++;
		this.sendSummation+=payload;
		//System.out.println("Sent: "+msg);
		sendMessage(msg);
	}
	public void sendMessage(Message msg) {
		InetSocketAddress nextNodeOnRoute = routingHandler.getNextNodeInRoute(msg.getDestination());
		TCPSender senderToNextNode = this.contacts.get(nextNodeOnRoute);
		senderToNextNode.sendData(msg.getBytes());
	}
	
	private void onTrafficSummaryRequest(TrafficSummaryRequest trafficSummaryRequest) {
		TrafficSummaryResponse response = new TrafficSummaryResponse(this.ownAddress.getAddress().getAddress(),this.ownAddress.getPort(),
				sendTracker, sendSummation,
				recieveTracker, recieveSummation,
				relayTracker);
		
		registrySender.sendData(response.getBytes());
		
		recieveTracker = 0;
		sendTracker = 0;
		relayTracker = 0;
		
		recieveSummation = 0;
		sendSummation = 0;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String registryHost = args[0];
		int registryPort = Integer.parseInt(args[1]);
		try {
			MessagingNode m = new MessagingNode(registryHost,registryPort);
			Thread sthread = new Thread(new MessagingNodeCommandInterpreter(m));
			sthread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	


}

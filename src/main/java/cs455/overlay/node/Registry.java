package cs455.overlay.node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

import cs455.overlay.transport.*;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;

public class Registry implements Node {
	
	TCPServerThread serverThread = null;
	
	private Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights=null;
	public Map<InetSocketAddress, Map<InetSocketAddress,Integer>> getLinkWeights() throws Exception{
		if (linkWeights == null) {
			throw new Exception("Link weights are not yet assigned! Setup overlay properly prior to doing that!");
		}
		return linkWeights;
	}
	
	private Map<InetSocketAddress, Collection<InetSocketAddress>> connectionsTable;
	Map<InetSocketAddress,TCPSender> registeredNodes = new HashMap<InetSocketAddress,TCPSender>();
	public Set<InetSocketAddress> getRegisteredNodes() {
		return registeredNodes.keySet();
	}
	Map<InetSocketAddress, Boolean> reportedDone = null;
	ArrayList<TrafficSummaryResponse> gatheredStats = null;
	public Registry(int registryPort) throws IOException {		
		serverThread = new TCPServerThread(registryPort, this);
		Thread sthread = new Thread(serverThread);
		sthread.start();							
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		switch (event.getType()) {
		case REGISTER_REQUEST: 
			onRegistrationRequestRecieved((Register)event);
			break;
		case TASK_COMPLETE: 
			onTaskCompleteRecieved((TaskComplete)event);
			break;
		case TRAFFIC_SUMMARY: 
			onTrafficSummaryRecieved((TrafficSummaryResponse)event);
			break;
		default:
			throw new Exception("Event of this type is not supported");
		}
		
	}
	private void onRegistrationRequestRecieved(Register registrationRequest) throws UnknownHostException {
		System.out.println(registrationRequest);
		InetSocketAddress address = NodeUtilHelpers.constructAddress(registrationRequest.getRegisteringIp(),registrationRequest.getRegisteringPort());
		// check if the node has been previously registered
		if (registeredNodes.containsKey(address)) {
			registeredNodes.get(address).sendData(new Register(false,"This node has already been registered").getBytes());
			return;
		}
		if (!registrationRequest.IPverified) {
			try {
				(new TCPSender(address)).sendData(new Register(false,"Your IP mismatches the one in registration request").getBytes());
			} catch (IOException e) {
				System.out.println("Sending back registration complaints failed");
			}
			return;
		}
		
		try {
			registeredNodes.put(address, new TCPSender(address));
			registeredNodes.get(address).sendData(new Register(true).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			registeredNodes.get(address).sendData(new Register(false,e.getMessage()).getBytes());
		}
	}
	private void onTaskCompleteRecieved(TaskComplete taskCompleteReport) throws Exception {
		//System.out.println(taskCompleteReport);
		InetSocketAddress address = NodeUtilHelpers.constructAddress(taskCompleteReport.getIp(),taskCompleteReport.getPort());
		if(!reportedDone.containsKey(address))
			throw new Exception("Node "+address+" was not found in the reported done table");
		this.reportedDone.put(address,true);
		
		requestTrafficSummary();
	}
	private synchronized void requestTrafficSummary() {
		if (reportedDone.values().stream().allMatch(t -> t==true)) {
			TrafficSummaryRequest summaryRequest = new TrafficSummaryRequest();
			for(InetSocketAddress node : this.registeredNodes.keySet()) {
				registeredNodes.get(node).sendData(summaryRequest.getBytes());
				this.reportedDone.put(node,false);
			}
		}
	}
	private synchronized void onTrafficSummaryRecieved(TrafficSummaryResponse trafficSummary) {
		if (gatheredStats == null)
			gatheredStats = new ArrayList<TrafficSummaryResponse>();
		gatheredStats.add(trafficSummary);
		if(gatheredStats.size()==registeredNodes.size()) {
			StatisticsCollectorAndDisplay.printExperimentStats(gatheredStats);
		}
	}
	
public void setupOverlay(int numberOfConnections) {
		OverlayCreator overlayCreator = new OverlayCreator();
		this.connectionsTable = overlayCreator.buildConnectionsTable(registeredNodes.keySet(), numberOfConnections);
		for(InetSocketAddress node:connectionsTable.keySet()) {
			TCPSender sender = registeredNodes.get(node);
			MessagingNodesList instructions = new MessagingNodesList(connectionsTable.get(node));
			sender.sendData(instructions.getBytes());
		}
	}
	public void assignLinkWeights() {
		this.linkWeights = new HashMap<InetSocketAddress, Map<InetSocketAddress,Integer>>();
		for(InetSocketAddress srcNode : connectionsTable.keySet()) {
			linkWeights.put(srcNode, new HashMap<InetSocketAddress,Integer>());			
			for(InetSocketAddress destNode : connectionsTable.get(srcNode)) {
				int weight =new Random().nextInt(10)+1;
				linkWeights.get(srcNode).put(destNode,weight);
			}
		}
		LinkWeights messageLinkWeights = new LinkWeights(this.linkWeights);
		for(TCPSender nodeCommunicator : this.registeredNodes.values()) {
			nodeCommunicator.sendData(messageLinkWeights.getBytes());
		}
	}
	public void startMessageExchange(int numberOfRounds) {
		TaskInitiate taskMessage = new TaskInitiate(numberOfRounds);
		for(TCPSender nodeCommunicator : this.registeredNodes.values()) {
			nodeCommunicator.sendData(taskMessage.getBytes());
		}
		//initiate status checking map if it did not exist earlier
		this.reportedDone=new HashMap<InetSocketAddress, Boolean>();
		for(InetSocketAddress node : registeredNodes.keySet()) 
			this.reportedDone.put(node, false);
				
	}

	public static void main(String[] args) {
		int port = Integer.parseUnsignedInt(args[0]);
		try {			
			Registry registry = new Registry(port);
			
			Thread sthread = new Thread(new RegistryCommandInterpreter(registry));
			sthread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
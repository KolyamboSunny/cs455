package cs455.overlay.node;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import cs455.overlay.wireformats.TrafficSummaryResponse;

public class SystemFeaturesTest {
	int registryPort = 6666;
	String registryIp;
	int numberOfMessagingNodes = 5;
	int numberOfConnections = 4;
	Registry registry;
	
	List<MessagingNode> messagingNodes;

	private void registrationTest() throws IOException, InterruptedException {
		registry = new Registry(registryPort);
		String registryIp = registry.serverThread.getAddress().getHostAddress();
		
		messagingNodes = new ArrayList<MessagingNode>();
		for(int nodeIndex=0;nodeIndex<numberOfMessagingNodes;nodeIndex++) {
			messagingNodes.add(new MessagingNode(registryIp, registryPort));
		}
		Thread.sleep(2000);
		assert registry.registeredNodes.size() == numberOfMessagingNodes;
	}
	
	private void deregistrationTest() throws IOException, InterruptedException {
		MessagingNode toDeregister = new MessagingNode(registryIp, registryPort);
		Thread.sleep(2000);
		assert registry.registeredNodes.size() == numberOfMessagingNodes+1;
		toDeregister.deregister(registryIp, registryPort);
		Thread.sleep(2000);
		assert registry.registeredNodes.size() == numberOfMessagingNodes;
	}

	private void setupOverlayTest() throws IOException, InterruptedException {
		
		registry.setupOverlay(numberOfConnections);
		//wait for 2 seconds
		Thread.sleep(2000);
		for(MessagingNode node: messagingNodes) {
			assert node.contacts.size()== numberOfConnections;
		}
	}
	
	private void assignLinkWeights() throws InterruptedException{
		registry.assignLinkWeights();
		Thread.sleep(2000);
		
	}
	
	private void testDirectMessaging(MessagingNode nodeFrom, MessagingNode nodeTo) throws InterruptedException, UnknownHostException{
		int recieverBefore = nodeTo.recieveTracker;
		int senderBefore = nodeFrom.recieveTracker;
		long recievedSummationBefore = nodeTo.recieveSummation;
		long sendSummationBefore = nodeFrom.sendSummation;
		
		nodeFrom.sendMessage(nodeTo.ownAddress);
		Thread.sleep(2000);
		int recieverAfter = nodeTo.recieveTracker;
		int senderAfter = nodeFrom.sendTracker;
		long recievedSummationAfter = nodeTo.recieveSummation;
		long sendSummationAfter = nodeFrom.sendSummation;
		
		assert recieverAfter == recieverBefore+1;
		assert senderAfter == senderBefore+1;
		assert (sendSummationAfter-sendSummationBefore) == (recievedSummationAfter-recievedSummationBefore);
	}
	
	private void testBroadMessaging() throws InterruptedException {
		registry.startMessageExchange(5);
		Thread.sleep(20000);
		ArrayList<TrafficSummaryResponse> nodeStats = registry.gatheredStats;
		Thread.sleep(1000);
		int sentCounter=0, recieveCounter=0;
		long sentSummation=0, recieveSummation=0;
		
		for(int nodeIndex=0;nodeIndex<nodeStats.size();nodeIndex++){
			TrafficSummaryResponse stat = nodeStats.get(nodeIndex);
	        sentCounter+=stat.sentCounter;
	        recieveCounter+=stat.recieveCounter;
			sentSummation+=stat.sentSummation;
			recieveSummation+=stat.recieveSummation;
	    }
		
		assert sentCounter == recieveCounter;
        assert sentSummation == recieveSummation;	
	}
	@Test
	public void systemTest() throws IOException, InterruptedException {
		registrationTest();
		deregistrationTest();
		setupOverlayTest();
		assignLinkWeights();
		//testDirectMessaging(messagingNodes.get(0),messagingNodes.get(3));
		testBroadMessaging();
	}
}

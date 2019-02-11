package cs455.overlay.node;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class SystemFeaturesTest {
	int registryPort = 6666;
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
	
	@Test
	public void systemTest() throws IOException, InterruptedException {
		registrationTest();
		setupOverlayTest();
		assignLinkWeights();
	}
}

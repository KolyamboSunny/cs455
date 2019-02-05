package cs455.overlay.util;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import cs455.overlay.util.OverlayCreator;

public class OverlayCreatorTest {

	private Collection<InetSocketAddress> getMockRegisteredNodes(int numberOfNodes) {
		Collection<InetSocketAddress> registeredNodes = new HashSet<InetSocketAddress>();
		for(;numberOfNodes>0;numberOfNodes--) {
			InetSocketAddress node= new InetSocketAddress("192.168.0."+numberOfNodes,2000+numberOfNodes);
			registeredNodes.add(node);
		}
		return registeredNodes;
	}
	
	@Test
	public void testBuildConnectionsTable() {
		int desiredNumberOfConnections = 4;
		int numberOfNodes = 25;
		boolean foundNodeWithLessConnections = false;
		
		Collection<InetSocketAddress> registeredNodes = getMockRegisteredNodes(numberOfNodes);
		OverlayCreator overlayCreator = new OverlayCreator();
		HashMap<InetSocketAddress,Collection<InetSocketAddress>> connectionTable = overlayCreator.buildConnectionsTable(registeredNodes, desiredNumberOfConnections);
		
		for(InetSocketAddress countedNode : registeredNodes) {
			int numberOfConnections = overlayCreator.getNumberOfConnections(countedNode);
			
			//assert numberOfConnections to be as expected
			assert (numberOfConnections == desiredNumberOfConnections) || foundNodeWithLessConnections;
			assert !foundNodeWithLessConnections || (numberOfConnections == desiredNumberOfConnections-1);
		}
	}

}

package cs455.overlay.util;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class OverlayCreator {
	HashMap<InetSocketAddress, Collection<InetSocketAddress>> connectionTable;
	HashMap<InetSocketAddress, Integer> numberOfConnections;
	public int getNumberOfConnections(InetSocketAddress countedNode) {
		return numberOfConnections.get(countedNode);
	}
	
	private void establishConnection(InetSocketAddress srcNode, InetSocketAddress destNode) {
		connectionTable.get(srcNode).add(destNode);
		
		int countSrc = numberOfConnections.get(srcNode);
		numberOfConnections.put(srcNode, countSrc + 1);
		
		int countDest = numberOfConnections.get(destNode);
		numberOfConnections.put(destNode, countDest + 1);
		
	}
	
 	public HashMap<InetSocketAddress,Collection<InetSocketAddress>> buildConnectionsTable(Collection<InetSocketAddress> registeredNodes, int desiredNumberOfConnections){
		//initiate connection table for all registered nodes
		this.connectionTable = new HashMap<InetSocketAddress,Collection<InetSocketAddress>>();
		this.numberOfConnections = new HashMap<InetSocketAddress, Integer>();
		for(InetSocketAddress node: registeredNodes) {
			connectionTable.put(node, new ArrayList<InetSocketAddress>());
			numberOfConnections.put(node, 0);
		}
		InetSocketAddress[] nodeArray = registeredNodes.toArray(new InetSocketAddress[registeredNodes.size()]);
		boolean isLastRound = false;
		//set up connections in rounds: offset increases by 1 each time
		for(int connectionRound=1; !isLastRound; connectionRound++) {
			//set up round connections to avoid partition
			for(int srcNodeIndex = 0; srcNodeIndex < registeredNodes.size(); srcNodeIndex++) {
				int destNodeIndex = srcNodeIndex + connectionRound;
				//if reached the end, go to the beginning in round fashion
				if (destNodeIndex >= nodeArray.length) destNodeIndex-=nodeArray.length;
				
				InetSocketAddress srcNode= nodeArray[srcNodeIndex];
				InetSocketAddress destNode= nodeArray[destNodeIndex];
				//abort operation if the connection gap reached
				if(getNumberOfConnections(srcNode)>=desiredNumberOfConnections || getNumberOfConnections(destNode)>=desiredNumberOfConnections) {
					isLastRound = true;
					continue;
				}
				else {
					establishConnection(srcNode,destNode);
				}
			}
		}
		return null;
	}

}

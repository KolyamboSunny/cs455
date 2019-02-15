package cs455.overlay.dijkstra;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import cs455.overlay.util.OverlayGraph;

public class ShortestPath {	
	public static List<InetSocketAddress> computeShortestRoute(InetSocketAddress source, InetSocketAddress dest,OverlayGraph graph) {
		Map<InetSocketAddress,Integer> distances = new HashMap<InetSocketAddress,Integer>();
		for(InetSocketAddress node: graph.getNodes()) {
			distances.put(node, Integer.MAX_VALUE);
		}
		distances.put(source, 0);
		Map<InetSocketAddress,InetSocketAddress> parents = new HashMap<InetSocketAddress,InetSocketAddress>();
	    Set<InetSocketAddress> settledNodes = new HashSet<>();
	    Set<InetSocketAddress> unsettledNodes = new HashSet<>();
	    
	    unsettledNodes.add(source);
	 
	    while (!unsettledNodes.isEmpty()) {
	        InetSocketAddress currentNode = getLowestDistanceNode(unsettledNodes,distances);
	        unsettledNodes.remove(currentNode);
	        for (Entry<InetSocketAddress, Integer> adjacentNodeAndDistance: graph.getNeighborsWithDistances(currentNode).entrySet()) {
	            InetSocketAddress adjacentNode = adjacentNodeAndDistance.getKey();
	            Integer edgeWeight = adjacentNodeAndDistance.getValue();
	            if (!settledNodes.contains(adjacentNode)) {
	            	Integer sourceDistance = distances.get(currentNode);
				    if (sourceDistance + edgeWeight < distances.get(adjacentNode)) {
				        distances.put(adjacentNode,sourceDistance + edgeWeight);
				        parents.put(adjacentNode, currentNode);				        
				    }
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return backtraceShortestRoute(source,dest,parents);
	}

	private static InetSocketAddress getLowestDistanceNode(Set<InetSocketAddress> unsettledNodes, Map<InetSocketAddress,Integer> distances) {
		InetSocketAddress lowestDistanceNode = null;
	    int lowestDistance = Integer.MAX_VALUE;
	    for (InetSocketAddress node: unsettledNodes) {
	        int nodeDistance = distances.get(node);
	        if (nodeDistance < lowestDistance) {
	            lowestDistance = nodeDistance;
	            lowestDistanceNode = node;
	        }
	    }
	    return lowestDistanceNode;
	}
	private static List<InetSocketAddress> backtraceShortestRoute(InetSocketAddress source,InetSocketAddress destination,Map<InetSocketAddress,InetSocketAddress> parents){
		ArrayList<InetSocketAddress> shortestPathStack = new ArrayList<InetSocketAddress>();
		InetSocketAddress currentNode = destination;
		shortestPathStack.add(destination);
		while (!currentNode.equals(source)) {
			currentNode = parents.get(currentNode);
			shortestPathStack.add(currentNode);
		}
		Collections.reverse(shortestPathStack);
		return shortestPathStack;
	}
}

package cs455.overlay.dijkstra;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cs455.overlay.util.OverlayGraph;

public class ShortestPathTest {
	private ArrayList<InetSocketAddress> getMockRegisteredNodes(int numberOfNodes) {
		ArrayList<InetSocketAddress> registeredNodes = new ArrayList<InetSocketAddress>();
		for(;numberOfNodes>0;numberOfNodes--) {
			InetSocketAddress node= new InetSocketAddress("192.168.0."+numberOfNodes,2000+numberOfNodes);
			registeredNodes.add(node);
		}
		return registeredNodes;
	}
	private Map<InetSocketAddress,Map<InetSocketAddress,Integer>> getMockLinkWeights(final ArrayList<InetSocketAddress> nodes) {
		HashMap<InetSocketAddress,Map<InetSocketAddress,Integer>> graph = new HashMap<InetSocketAddress,Map<InetSocketAddress,Integer>>();
		
		addConnection('A','B',4,nodes,graph);
		addConnection('A','C',2,nodes,graph);
		addConnection('B','C',1,nodes,graph);
		addConnection('B','D',5,nodes,graph);
		addConnection('C','D',8,nodes,graph);
		addConnection('C','E',10,nodes,graph);
		addConnection('D','E',2,nodes,graph);
		addConnection('D','F',6,nodes,graph);
		addConnection('E','F',3,nodes,graph);
		return graph;
	}
	private void addConnection(char node0,char node1, int weight,ArrayList<InetSocketAddress> nodes, Map<InetSocketAddress,Map<InetSocketAddress,Integer>> graph) {
		addConnection(node0-'A',node1-'A',weight,nodes,graph);
	}
	private void addConnection(int nodeId0,int nodeId1, int weight,ArrayList<InetSocketAddress> nodes, Map<InetSocketAddress,Map<InetSocketAddress,Integer>> graph) {
		if(!graph.containsKey(nodes.get(nodeId0)))
			graph.put(nodes.get(nodeId0),new HashMap<InetSocketAddress,Integer>());
		
		graph.get(nodes.get(nodeId0)).put(nodes.get(nodeId1), weight);
	}
	
	@Test
	public void testShortestPath() throws Exception {
		final ArrayList<InetSocketAddress> nodes = getMockRegisteredNodes(6);
		OverlayGraph graph = new OverlayGraph(getMockLinkWeights(nodes));
		List<InetSocketAddress> shortestPath = ShortestPath.computeShortestRoute(nodes.get('A'-'A'), nodes.get('F'-'A'), graph);
		assert shortestPath.get(0).toString().equals(nodes.get('A'-'A').toString());
		assert shortestPath.get(1).toString().equals(nodes.get('C'-'A').toString());
		assert shortestPath.get(2).toString().equals(nodes.get('B'-'A').toString());
		assert shortestPath.get(3).toString().equals(nodes.get('D'-'A').toString());
		assert shortestPath.get(4).toString().equals(nodes.get('E'-'A').toString());
		assert shortestPath.get(5).toString().equals(nodes.get('F'-'A').toString());
	}

}

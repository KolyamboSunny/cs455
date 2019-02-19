package cs455.overlay.util;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OverlayGraph {
	//private Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights;
	private Map<InetSocketAddress, Map<InetSocketAddress,Integer>> duplexGraph;
	
	public OverlayGraph(Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights) throws Exception {		
		duplexGraph = new HashMap<InetSocketAddress, Map<InetSocketAddress,Integer>>();
		for(InetSocketAddress node1 : linkWeights.keySet()) {
			for(InetSocketAddress node2 : linkWeights.get(node1).keySet()) {
				int weight = linkWeights.get(node1).get(node2);
				addDuplexConnection(node1, node2, weight);
			}
		}
	}
	private void addDuplexConnection(InetSocketAddress node1, InetSocketAddress node2, int weight) throws Exception {
		addConnection(node1,node2,weight);
		addConnection(node2,node1,weight);
	}
	private void addConnection(InetSocketAddress node1, InetSocketAddress node2, int weight) throws Exception {
		if (!duplexGraph.containsKey(node1))
			duplexGraph.put(node1, new HashMap<InetSocketAddress,Integer>());
		else
		{
			if(duplexGraph.get(node1).containsKey(node2))
				throw new Exception("This connection has already been added");			
		}
		duplexGraph.get(node1).put(node2,weight);
	}
	
	public boolean directLinkExists(InetSocketAddress node1, InetSocketAddress node2) {
		return duplexGraph.containsKey(node1)&&duplexGraph.get(node1).containsKey(node2);		
	}
	public int linkWeight(InetSocketAddress node1, InetSocketAddress node2) throws Exception {
		if(!directLinkExists(node1,node2))
			throw new Exception("Direct link between the provided nodes does not exist. Check link validity before requesting it!");
		return duplexGraph.get(node1).get(node2);
	}
	public Collection<InetSocketAddress> getNodes(){
		return duplexGraph.keySet();
	}
	public InetSocketAddress getRandomNode(InetSocketAddress self) throws Exception{
		Collection<InetSocketAddress> allNodes = new HashSet(this.getNodes());
		boolean foundSelf = allNodes.remove(self);
		if(!foundSelf)
			throw new Exception("Could not find itself among the known nodes");
		//randomly select an index of a node to return
		int toSelectIndex = (int) (Math.random() * allNodes.size());
		//iterate through the collection until the randomly selected node is reached
		for(InetSocketAddress node: allNodes) 
			if (--toSelectIndex < 0) 
				return node;
		throw new Exception("The only known node was itself");		
	}
	public Map<InetSocketAddress,Integer> getNeighborsWithDistances(InetSocketAddress node){
		return duplexGraph.get(node);
	}
}

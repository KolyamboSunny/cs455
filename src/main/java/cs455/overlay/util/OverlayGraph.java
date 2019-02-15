package cs455.overlay.util;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
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
	public Map<InetSocketAddress,Integer> getNeighborsWithDistances(InetSocketAddress node){
		return duplexGraph.get(node);
	}
}

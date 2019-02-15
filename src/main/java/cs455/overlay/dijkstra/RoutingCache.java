package cs455.overlay.dijkstra;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs455.overlay.util.OverlayGraph;

public class RoutingCache {	
	Map<InetSocketAddress,List<InetSocketAddress>> cachedRoutes= new HashMap<InetSocketAddress,List<InetSocketAddress>>();
	InetSocketAddress source;
	OverlayGraph graph;
	public RoutingCache(OverlayGraph graph, InetSocketAddress source) {
		this.graph = graph;
		this.source = source;
	}
	public List<InetSocketAddress> computeShortestRoute(InetSocketAddress destination) {
		if(!this.cachedRoutes.containsKey(destination))
			cachedRoutes.put(destination, ShortestPath.computeShortestRoute(source, destination, graph));
		return cachedRoutes.get(destination);
	}

}

package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class LinkWeights implements Event {
	private Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights;
	public Map<InetSocketAddress, Map<InetSocketAddress,Integer>> getLinkWeights(){
		return this.linkWeights;
	}
	private int countNumberOfLinks() {
		int numLinks=0;
		for (InetSocketAddress srcNode: this.linkWeights.keySet()) {
			numLinks+=this.linkWeights.get(srcNode).size();
		}
		return numLinks;
	}
	
	public LinkWeights(Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights) {
		this.linkWeights = linkWeights;
	}
	
	@Override
	public EventType getType() {		
		return EventType.LINK_WEIGHTS;
	}
	
	public LinkWeights(byte[] encoded) {
		ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
		try {
			@SuppressWarnings("unused")
			EventType type = EventType.values()[bis.read()];
			int length = ByteEncoder.readEncodedInt(bis);
			this.linkWeights = new HashMap<InetSocketAddress, Map<InetSocketAddress,Integer>>();
			for(;length>0;length--) {
				InetSocketAddress srcNode = ByteEncoder.readEncodedAddress(bis);
				InetSocketAddress destNode = ByteEncoder.readEncodedAddress(bis);
				int weight = ByteEncoder.readEncodedInt(bis);
				this.linkWeights.put(srcNode, new HashMap<InetSocketAddress,Integer>());
				this.linkWeights.get(srcNode).put(destNode,weight);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(getType().ordinal());
			ByteEncoder.writeEncodedInt(this.countNumberOfLinks(), bos);
			for (InetSocketAddress srcNode: this.linkWeights.keySet()) {
				for (InetSocketAddress destNode: this.linkWeights.get(srcNode).keySet()) {
					ByteEncoder.writeEncodedAddress(srcNode, bos);
					ByteEncoder.writeEncodedAddress(destNode, bos);
					ByteEncoder.writeEncodedInt(this.linkWeights.get(srcNode).get(destNode), bos);
				}
			}
			bos.flush();
			encodedEvent = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return encodedEvent;
	}

	public String toString() {
		String result = "Type: LINK_WEIGHTS" +"\t";
		result+="Number of Nodes: "+linkWeights.size()+"\n";
		for (InetSocketAddress srcNode: this.linkWeights.keySet()) {
			for (InetSocketAddress destNode: this.linkWeights.get(srcNode).keySet()) {
				result+= srcNode.toString() + " "+destNode.toString();
				result+=" "+this.linkWeights.get(srcNode).get(destNode)+"\n";
			}
		}
		return result;
	}
}

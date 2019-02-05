package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;

public class MessagingNodesList implements Event {
	public Collection<InetSocketAddress> destinations;
	
	public MessagingNodesList(Collection<InetSocketAddress> destinations) {
		this.destinations = destinations;
	}
	

	@Override
	public EventType getType() {		
		return EventType.MESSAGING_NODES_LIST;
	}
	
	public MessagingNodesList(byte[] encoded) {
		ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
		try {
			EventType type = EventType.values()[bis.read()];
			int length = ByteEncoder.readEncodedInt(bis);
			this.destinations = new HashSet<InetSocketAddress>();
			for(;length>0;length--) {
				destinations.add(ByteEncoder.readEncodedAddress(bis));
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
			ByteEncoder.writeEncodedInt(destinations.size(), bos);
			for (InetSocketAddress node:destinations) {
				ByteEncoder.writeEncodedAddress(node, bos);				
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
		String result = "Type: MESSAGING_NODES_LIST" +"\t";
		result+="Number of Nodes: "+destinations.size()+"\n";
		for (InetSocketAddress node:destinations) {
			result+= node.toString() + "\n";
		}
		return result;
	}
}

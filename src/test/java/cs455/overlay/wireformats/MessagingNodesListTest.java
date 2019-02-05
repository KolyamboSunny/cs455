package cs455.overlay.wireformats;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

@SuppressWarnings("unused")
public class MessagingNodesListTest {
	
	private MessagingNodesList getMockMessagingNodesList() {
		HashSet<InetSocketAddress> destinations = new HashSet<InetSocketAddress>();
		destinations.add(new InetSocketAddress("127.0.0.1",2001));
		destinations.add(new InetSocketAddress("127.0.0.2",2002));
		destinations.add(new InetSocketAddress("127.0.0.3",2003));
		destinations.add(new InetSocketAddress("127.0.0.4",2004));
		destinations.add(new InetSocketAddress("127.0.0.5",2005));
		return new MessagingNodesList(destinations);
	}
	
	@Test
	public void testMessagingNodesListDemarshalling() throws Exception {			
		//test registration request
		MessagingNodesList toMarshall = getMockMessagingNodesList();
		byte[] encoded = toMarshall.getBytes();		
		MessagingNodesList demarshalled = new MessagingNodesList(encoded);
		
		for (InetSocketAddress node:toMarshall.destinations)
			  assert (demarshalled.destinations.contains(node));
		
	}
	@Test
	public void testRegisterFactory() throws Exception {		
		MessagingNodesList toMarshall = getMockMessagingNodesList();
		byte[] encoded = toMarshall.getBytes();	
		Event recognizedEvent = EventFactory.getEvent(encoded);
		MessagingNodesList demarshalled = (MessagingNodesList)recognizedEvent;
		
		for (InetSocketAddress node:toMarshall.destinations)
			  assert (demarshalled.destinations.contains(node));
	}
}

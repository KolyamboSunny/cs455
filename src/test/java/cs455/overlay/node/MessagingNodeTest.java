package cs455.overlay.node;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class MessagingNodeTest {

	@Test
	public void testMessagingNode() throws IOException {
		MessagingNode m1 = new MessagingNode();	
		MessagingNode m2 = new MessagingNode();	
		
		m1.sendMessage(m2.getServerAddress(), 1488);
		
		fail("Not yet implemented");
	}

	@Test
	public void testOnEvent() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetServerAddress() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddContactsEntry() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegister() {
		fail("Not yet implemented");
	}

}

package cs455.overlay.wireformats;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("unused")
public class TestDemarshalling {
	
	private Register getMockRegister() {
		byte[] ipAddress = {127, 0, 0, 1};
		return new Register(ipAddress,2000);
	}
	@Test
	public void testRegisterDemarshalling() throws Exception {			
		Register toMarshall = getMockRegister();
		byte[] encoded = toMarshall.getBytes();
		
		Register demarshalled = new Register(encoded);
		assert (Arrays.equals(demarshalled.getRegisteringIp(),toMarshall.getRegisteringIp()));
		assert demarshalled.getRegisteringPort() == toMarshall.getRegisteringPort();
	}
	@Test
	public void testRegisterFactory() throws Exception {			
		Register toMarshall = getMockRegister();
		byte[] encoded = toMarshall.getBytes();
		
		Event recognizedEvent = EventFactory.getEvent(encoded);
		Register demarshalled = (Register)recognizedEvent;
		assert (Arrays.equals(demarshalled.getRegisteringIp(),toMarshall.getRegisteringIp()));
		assert demarshalled.getRegisteringPort() == toMarshall.getRegisteringPort();
	}
}

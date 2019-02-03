package cs455.overlay.wireformats;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("unused")
public class RegisterMsgTest {
	
	private Register getMockRegisterRequest() {
		byte[] ipAddress = {127, 0, 0, 1};
		return new Register(ipAddress,2000);
	}
	
	private Register getMockRegisterResponse() {
		//imagine, registration failed. so that an error message would be included
		boolean success = false;
		String errorMsg = "Registration failed";
		return new Register(success,errorMsg);
	}
	
	@Test
	public void testRegisterDemarshalling() throws Exception {			
		//test registration request
		Register toMarshall = getMockRegisterRequest();
		byte[] encoded = toMarshall.getBytes();		
		Register demarshalled = new Register(encoded);
		assert (Arrays.equals(demarshalled.getRegisteringIp(),toMarshall.getRegisteringIp()));
		assert demarshalled.getRegisteringPort() == toMarshall.getRegisteringPort();
		
		//test registration response
		toMarshall = getMockRegisterResponse();
		encoded = toMarshall.getBytes();		
		demarshalled = new Register(encoded);
		assert demarshalled.getIsSuccessful() == toMarshall.getIsSuccessful();
		assert demarshalled.getAdditionalInfo().equals(toMarshall.getAdditionalInfo());
	}
	@Test
	public void testRegisterFactory() throws Exception {		
		//test registration request
		Register toMarshall = getMockRegisterRequest();
		byte[] encoded = toMarshall.getBytes();		
		Event recognizedEvent = EventFactory.getEvent(encoded);
		Register demarshalled = (Register)recognizedEvent;
		assert (Arrays.equals(demarshalled.getRegisteringIp(),toMarshall.getRegisteringIp()));
		assert demarshalled.getRegisteringPort() == toMarshall.getRegisteringPort();
		
		//test registration response
		toMarshall = getMockRegisterResponse();
		encoded = toMarshall.getBytes();		
		recognizedEvent = EventFactory.getEvent(encoded);
		demarshalled = (Register)recognizedEvent;
		assert demarshalled.getIsSuccessful() == toMarshall.getIsSuccessful();
		assert demarshalled.getAdditionalInfo().equals(toMarshall.getAdditionalInfo());
	}
}

package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

public class Registry implements Node {
	
	TCPServerThread serverThread = null;
	HashMap<InetSocketAddress,TCPSender> registeredNodes = new HashMap<InetSocketAddress,TCPSender>();
	public Registry(int registryPort) throws IOException {		

		serverThread = new TCPServerThread(registryPort, this);
		Thread sthread = new Thread(serverThread);
		sthread.start();							
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		switch (event.getType()) {
		case REGISTER: 
			onRegistrationRequestRecieved((Register)event);
			break;
		default:
			throw new Exception("Event of this type is not supported");
			//break;
		}
		
	}
	private void onRegistrationRequestRecieved(Register registrationRequest) throws UnknownHostException {
		System.out.println(registrationRequest);
		InetAddress registeredAddress = InetAddress.getByAddress(registrationRequest.getRegisteringIp());
		InetSocketAddress address = new InetSocketAddress(registeredAddress,registrationRequest.getRegisteringPort());
		try {
			registeredNodes.put(address, new TCPSender(address));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		int port = Integer.parseUnsignedInt(args[0]);
		try {
			@SuppressWarnings("unused")
			Registry r = new Registry(port);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

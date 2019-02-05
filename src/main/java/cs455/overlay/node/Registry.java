package cs455.overlay.node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

import cs455.overlay.transport.*;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.*;

public class Registry implements Node {
	
	TCPServerThread serverThread = null;
	
	HashMap<InetSocketAddress,TCPSender> registeredNodes = new HashMap<InetSocketAddress,TCPSender>();
	public Set<InetSocketAddress> getRegisteredNodes() {
		return registeredNodes.keySet();
	}
	
	public Registry(int registryPort) throws IOException {		
		serverThread = new TCPServerThread(registryPort, this);
		Thread sthread = new Thread(serverThread);
		sthread.start();							
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		switch (event.getType()) {
		case REGISTER_REQUEST: 
			onRegistrationRequestRecieved((Register)event);
			break;
		default:
			throw new Exception("Event of this type is not supported");
		}
		
	}
	private void onRegistrationRequestRecieved(Register registrationRequest) throws UnknownHostException {
		System.out.println(registrationRequest);
		InetSocketAddress address = NodeUtilHelpers.constructAddress(registrationRequest.getRegisteringIp(),registrationRequest.getRegisteringPort());
		// check if the node has been previously registered
		if (registeredNodes.containsKey(address)) {
			registeredNodes.get(address).sendData(new Register(false,"This node has already been registered").getBytes());
			return;
		}
		//TODO: check if the node ip matches the one in the registration request

		try {
			registeredNodes.put(address, new TCPSender(address));
			registeredNodes.get(address).sendData(new Register(true).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			registeredNodes.get(address).sendData(new Register(false,e.getMessage()).getBytes());
		}
	}
	
	public void setupOverlay(int numberOfConnections) {
		OverlayCreator overlayCreator = new OverlayCreator();
		Map<InetSocketAddress,Collection<InetSocketAddress>> connectionsTable = overlayCreator.buildConnectionsTable(registeredNodes.keySet(), numberOfConnections);
		for(InetSocketAddress node:connectionsTable.keySet()) {
			TCPSender sender = registeredNodes.get(node);
			MessagingNodesList instructions = new MessagingNodesList(connectionsTable.get(node));
			sender.sendData(instructions.getBytes());
		}
	}
	
	public static void main(String[] args) {
		int port = Integer.parseUnsignedInt(args[0]);
		try {			
			Registry registry = new Registry(port);
			
			Thread sthread = new Thread(new RegistryCommandInterpreter(registry));
			sthread.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
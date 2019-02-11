package cs455.overlay.node;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class RegistryCommandInterpreter implements Runnable{
	
	private enum CommandCode {		
		list_messaging_nodes,
		setup_overlay, send_overlay_link_weights
		}
	private CommandCode getCommandCode(String command) throws Exception {
		if (command.equalsIgnoreCase("list-messaging-nodes")) return CommandCode.list_messaging_nodes;
		if (command.equalsIgnoreCase("setup-overlay")) return CommandCode.setup_overlay;
		if (command.equalsIgnoreCase("send-overlay-link-weights")) return CommandCode.send_overlay_link_weights;
		throw new Exception("Command is not recognized");
	}
	
	private boolean wantToLive = true;
	private Registry registry;
	public RegistryCommandInterpreter(Registry registry) {
		this.registry = registry;
	}
	
	
	public void printMessagingNodes() {
		for(InetSocketAddress registeredNode : registry.getRegisteredNodes() ) {
			String nodeInfo= registeredNode.getHostString()+":"+registeredNode.getPort(); 
			System.out.println(nodeInfo);
		}
	}
	
	@Override
	public void run() {
	    Scanner scanner = new Scanner(System.in);		    
	    
	    while(wantToLive) {
	    	String command = scanner.next();
	    	
	    	//try to interpret the command and print exception if the command is not known
	    	CommandCode commandCode;
	    	try {
	    		commandCode = getCommandCode(command);
	    	}
	    	catch(Exception e) {
	    		System.err.println(e.getMessage());
	    		continue;
	    	}
	    	
	    	switch (commandCode) {
	    	case list_messaging_nodes:
	    		printMessagingNodes();
	    		break;
	    	case setup_overlay:
	    		int numberOfConnections = scanner.nextInt();
	    		registry.setupOverlay(numberOfConnections);
	    		break;
	    	case send_overlay_link_weights:
	    		registry.assignLinkWeights();
	    		break;
	    	}
	    }
	    
	    scanner.close();
	}

}

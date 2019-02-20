package cs455.overlay.node;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Scanner;

public class RegistryCommandInterpreter implements Runnable{
	
	private enum CommandCode {		
		list_messaging_nodes, list_weights,
		setup_overlay, send_overlay_link_weights,
		start_message_exchange
		}
	private CommandCode getCommandCode(String command) throws Exception {
		if (command.equalsIgnoreCase("list-messaging-nodes")) return CommandCode.list_messaging_nodes;
		if (command.equalsIgnoreCase("setup-overlay")) return CommandCode.setup_overlay;
		if (command.equalsIgnoreCase("send-overlay-link-weights")) return CommandCode.send_overlay_link_weights;
		if (command.equalsIgnoreCase("list-weights")) return CommandCode.list_weights;
		if (command.equalsIgnoreCase("start")) return CommandCode.start_message_exchange;
		throw new Exception("Command is not recognized");
	}
	
	private boolean wantToLive = true;
	private Registry registry;
	public RegistryCommandInterpreter(Registry registry) {
		this.registry = registry;
	}
	
	private String printNode(InetSocketAddress node) {
		return node.getHostString()+":"+node.getPort(); 
	}
	public void printMessagingNodes() {
		for(InetSocketAddress registeredNode : registry.getRegisteredNodes() ) {
			String nodeInfo= printNode(registeredNode); 
			System.out.println(nodeInfo);
		}
	}
	public void printLinkWeights() {
		try {
			Map<InetSocketAddress, Map<InetSocketAddress,Integer>> linkWeights = registry.getLinkWeights();
			for(InetSocketAddress srcNode : linkWeights.keySet() ) {
				for(InetSocketAddress destNode:linkWeights.get(srcNode).keySet()) {
					int weight =linkWeights.get(srcNode).get(destNode); 
					String linkInfo = printNode(srcNode)+" "+printNode(destNode)+" "+weight;
					System.out.println(linkInfo);
				}				
			}
		} catch (Exception e) {
			System.err.println(e);
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
		    	case list_weights:
		    		printLinkWeights();
		    		break;	    		
		    	case setup_overlay:
		    		int numberOfConnections = scanner.nextInt();
		    		registry.setupOverlay(numberOfConnections);
		    		break;
		    	case send_overlay_link_weights:
		    		registry.assignLinkWeights();
		    		break;
		    	case start_message_exchange:
		    		int numberOfRounds = scanner.nextInt();
		    		registry.startMessageExchange(numberOfRounds);
		    		break;
	    	}
	    }
	    
	    scanner.close();
	}

}

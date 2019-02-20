package cs455.overlay.node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MessagingNodeCommandInterpreter implements Runnable{
	
	private enum CommandCode {		
		print_shortest_path, exit_overlay
		}
	private CommandCode getCommandCode(String command) throws Exception {
		if (command.equalsIgnoreCase("print-shortest-path")) return CommandCode.print_shortest_path;
		if (command.equalsIgnoreCase("exit-overlay")) return CommandCode.exit_overlay;
		throw new Exception("Command is not recognized");
	}
	
	private boolean wantToLive = true;
	private MessagingNode node;
	public MessagingNodeCommandInterpreter(MessagingNode node) {
		this.node = node;
	}
	
	public void printShortestPaths() {
		try {
			List<List<InetSocketAddress>> routes = node.getRoutes();
			for(List<InetSocketAddress> route : routes) {
				InetSocketAddress from = route.get(0);
				System.out.print(from);
				for(int nodeIndex =1; nodeIndex<route.size();nodeIndex++) {
					InetSocketAddress to = route.get(nodeIndex);
					int weight = node.overlayGraph.linkWeight(from, to);
					System.out.print("--"+weight+"--"+to);		
					from = to;
				}
				System.out.println();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
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
		    	case print_shortest_path:
		    		printShortestPaths();
		    		break;
		    	case exit_overlay:
				try {
					node.deregister();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
		    		break;	    		
	    	}
	    }
	    scanner.close();
	}

}

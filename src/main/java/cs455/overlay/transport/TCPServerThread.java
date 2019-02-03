package cs455.overlay.transport;

import java.io.IOException;
import java.net.*;

import cs455.overlay.node.Node;

public class TCPServerThread implements Runnable{
	private ServerSocket serverSocket = null;
	private Node node;
	private int port;
	public int getPort() {
		return this.port;
	}
	
	public TCPServerThread(Node node) throws IOException {
		//saving node reference to pass to a reciever thread and later call an onEvent method		
		this.node=node;
		
		// iterate through the ports, and try to initialize a socket on a first free port found.
		for (int port =1; port < 65000; port++) {
	        try {
	        	serverSocket = new ServerSocket(port);
	        	this.port = port;
	        	System.out.println("Server thread initialized: "+serverSocket.getLocalSocketAddress());
	        	return;
	        } catch (IOException ex) {
	            continue; // try next port
	        }
	    }
	    // no free port in a given range was found. Otherwise, method would have exited sooner.  
	    throw new java.io.IOException("no free port found");
	}
	public TCPServerThread(int port, Node node) throws IOException {
		//saving node reference to pass to a reciever thread and later call an onEvent method		
		this.node=node;
		
		// iterate through the ports, and try to initialize a socket on a first free port found.	
	        	serverSocket = new ServerSocket(port);
	        	this.port = port;
	        	System.out.println("Server thread initialized: "+serverSocket.getLocalSocketAddress());
	}
	
	public InetAddress getAddress() throws UnknownHostException {
		serverSocket.getInetAddress();
		return InetAddress.getLocalHost();
	}
	
	@Override
	public void run() {
		Socket clientSocket = null;
		while(true) {
			try {
				clientSocket = this.serverSocket.accept();
				
				TCPRecieverThread recieverThread = new TCPRecieverThread(clientSocket, node);
				Thread rthread = new Thread(recieverThread);
				rthread.start();
											
				//System.out.println("Client connected: "+clientSocket.getInetAddress());
			}
			catch (java.io.IOException e) {
				//TODO: implement notifying about exception
	            e.printStackTrace();
	        }
		}
    }
	
}

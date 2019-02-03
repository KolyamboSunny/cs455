package cs455.overlay.node;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class NodeUtilHelpers {

	
	public static InetSocketAddress constructAddress(byte[] ip, int port) throws UnknownHostException {
		InetAddress registeredAddress = InetAddress.getByAddress(ip);
		InetSocketAddress address = new InetSocketAddress(registeredAddress,port);
		return address;
	}
	

}

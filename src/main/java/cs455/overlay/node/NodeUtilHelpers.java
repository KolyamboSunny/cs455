package cs455.overlay.node;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class NodeUtilHelpers {

	
	public static InetSocketAddress constructAddress(byte[] ip, int port){
		InetAddress registeredAddress;
		try {
			registeredAddress = InetAddress.getByAddress(ip);
			InetSocketAddress address = new InetSocketAddress(registeredAddress,port);
			return address;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}
	

}

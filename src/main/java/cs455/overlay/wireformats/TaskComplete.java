package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TaskComplete implements Event{
	byte[] ip;
	public byte[] getIp() {
		return ip;
	}
	int port;
	public int getPort() {
		return port;
	}
	
 	public TaskComplete(byte[] ip, int port) { 		
		this.ip=ip;
		this.port = port;
	}
 
 	@Override
	public EventType getType() {
		return EventType.TASK_COMPLETE;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		bos.write(getType().ordinal());
		
		try {
			bos.write(ip);
			ByteEncoder.writeEncodedInt(port, bos);
			
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		encodedEvent = bos.toByteArray();		
		return encodedEvent;
	}
	public TaskComplete(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);		
		@SuppressWarnings("unused")
		EventType type = EventType.values()[bis.read()];
		
		this.ip = new byte[4];
		bis.read(this.ip, 0, 4);
		this.port=ByteEncoder.readEncodedInt(bis);
		
	}
	
	public String toString() {
		String result = "";
		result += "Message Type: "+this.getType()+"\t";
		try {
			result += "Node IP address: "+InetAddress.getByAddress(this.ip).getHostAddress()+"\t";
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result += "Node Port: "+this.port;

		return result;
		
	}
}

package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TrafficSummaryResponse implements Event{
	byte[] ip;
	public byte[] getIp() {
		return ip;
	}
	int port;
	public int getPort() {
		return port;
	}
	public int sentCounter, recieveCounter;
	public long sentSummation, recieveSummation;
	public int relayCounter;
	
 	public TrafficSummaryResponse(byte[] ip, int port,
 			int sentCounter, long sentSummation,
 			int recieveCounter, long recieveSummation,
 			int relayCounter) { 		
		this.ip=ip;
		this.port = port;
		
		this.sentCounter = sentCounter;
		this.sentSummation = sentSummation;
		
		this.recieveCounter = recieveCounter;
		this.recieveSummation = recieveSummation;
		
		this.relayCounter = relayCounter;
	}
 
 	@Override
	public EventType getType() {
		return EventType.TRAFFIC_SUMMARY;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] encodedEvent = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		bos.write(getType().ordinal());
		
		try {
			bos.write(ip);
			ByteEncoder.writeEncodedInt(port, bos);
			
			ByteEncoder.writeEncodedInt(sentCounter, bos);
			ByteEncoder.writeEncodedLong(sentSummation, bos);
			
			ByteEncoder.writeEncodedInt(recieveCounter, bos);
			ByteEncoder.writeEncodedLong(recieveSummation, bos);
			
			ByteEncoder.writeEncodedInt(relayCounter, bos);
			
			bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		encodedEvent = bos.toByteArray();		
		return encodedEvent;
	}
	public TrafficSummaryResponse(byte[] byteEncoding) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteEncoding);		
		@SuppressWarnings("unused")
		EventType type = EventType.values()[bis.read()];

		this.ip = new byte[4];
		bis.read(this.ip, 0, 4);
		
		this.port = ByteEncoder.readEncodedInt(bis);
		
		this.sentCounter = ByteEncoder.readEncodedInt(bis);
		this.sentSummation = ByteEncoder.readEncodedLong(bis);
		
		this.recieveCounter = ByteEncoder.readEncodedInt(bis);
		this.recieveSummation = ByteEncoder.readEncodedLong(bis);
		
		this.relayCounter = ByteEncoder.readEncodedInt(bis);
		bis.close();		
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
		
		result += "Node Port: "+this.port+"\t";
		
		result += "Number of messages sent: "+this.sentCounter+"\t";
		result += "Summation of sent messages: "+this.sentSummation+"\t";
		
		result += "Number of messages recieved: "+this.recieveCounter+"\t";
		result += "Summation of recieved messages: "+this.recieveSummation+"\t";
		
		result += "Number of messages relayed: "+this.relayCounter;
		
		return result;
		
	}
}

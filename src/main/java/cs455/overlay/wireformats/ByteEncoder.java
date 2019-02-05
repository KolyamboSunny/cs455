package cs455.overlay.wireformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import cs455.overlay.node.NodeUtilHelpers;

public class ByteEncoder {

	public static int readEncodedInt(ByteArrayInputStream bis) throws IOException {		
		byte[] encodedInt = new byte[Integer.BYTES];
		bis.read(encodedInt,0,Integer.BYTES);
		int decodedInt = java.nio.ByteBuffer.wrap(encodedInt).getInt();
		return decodedInt;
	}
	public static void writeEncodedInt(int toWrite, ByteArrayOutputStream bos) throws IOException {
		byte[] encodedInt = ByteBuffer.allocate(Integer.BYTES).putInt(toWrite).array();
		bos.write(encodedInt);
	}
	
	public static void writeEncodedAddress(InetSocketAddress toWrite, ByteArrayOutputStream bos) throws IOException {
		bos.write(toWrite.getAddress().getAddress());
		writeEncodedInt(toWrite.getPort(),bos);		
	}
	public static InetSocketAddress readEncodedAddress(ByteArrayInputStream bis) throws IOException {				
		byte[] ip = new byte[4];
		bis.read(ip,0,4);
		int port = readEncodedInt(bis);
		return NodeUtilHelpers.constructAddress(ip,port);
	}
	
	public static void writeEncodedString(String toWrite, ByteArrayOutputStream bos) throws IOException {
		byte[] encodedString = toWrite.getBytes();
		writeEncodedInt(encodedString.length,bos);
		bos.write(encodedString);
	}
	public static String readEncodedString(ByteArrayInputStream bis) throws IOException {		
		int length = readEncodedInt(bis);
		byte[] encodedString = new byte[length];
		bis.read(encodedString,0,length);
		String decodedString =  new String(encodedString, StandardCharsets.UTF_8);
		return decodedString;
	}
}

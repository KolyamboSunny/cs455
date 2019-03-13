package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SenderThread{
	
	SocketChannel channel;
	Selector selector;
	
	public SenderThread(SocketChannel channel, Selector selector) throws IOException {
		this.channel = channel;
		this.selector = selector;
	}
	
	public void sendSomething(byte[] toSend) throws IOException {		
		synchronized(channel) {
			try {
				channel.register(selector, SelectionKey.OP_WRITE);			
					
				ByteBuffer buffer = ByteBuffer.wrap(toSend);				
				while(buffer.hasRemaining()) {
					channel.write(buffer);
				}
				
				channel.register(selector,SelectionKey.OP_READ);
			}catch(Exception e) {
				System.err.println("Failed to send response to the client");
				channel.register(selector,SelectionKey.OP_READ);
			}
		}
	}
}

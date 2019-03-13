package cs455.scaling.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SenderThread implements Runnable{
	
	SocketChannel channel;
	Selector selector;
	
	public SenderThread(SocketChannel channel, Selector selector) throws IOException {
		this.channel = channel;
		this.selector = selector;
	}
	
	public void sendSomething(byte[] toSend) throws IOException {		
		synchronized(channel) {
			try {
			//	channel.register(selector, SelectionKey.OP_WRITE);			
			//	selector.select();
			//	Set<SelectionKey> keySet= selector.selectedKeys();
			//	Iterator<SelectionKey> keys = keySet.iterator();
			//	while(keys.hasNext()) {
			//		SelectionKey key = keys.next();
			//		if(key.isWritable()) {
						ByteBuffer buffer = ByteBuffer.wrap(toSend);
						
						while(buffer.hasRemaining()) {
							channel.write(buffer);
						}
						channel.register(selector,SelectionKey.OP_READ);
		//			}
		
		//		}
			}catch(Exception e) {
				System.err.println("Failed to send response to the client");
				channel.register(selector,SelectionKey.OP_READ);
			}
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}

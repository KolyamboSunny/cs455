package cs455.scaling.client;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import cs455.scaling.hash.Hash;

public class ResponseHandler implements Runnable {
	
	SocketChannel channel;
	Selector selector;
	LinkedList<Hash> sentHashes;
	ClientStatistics stats;
	int SHA1Length = 160/8;
	
	public ResponseHandler(SocketChannel channel, Selector selector,LinkedList<Hash> sentHashes, ClientStatistics stats){
		this.channel = channel;
		this.selector = selector;
		this.sentHashes = sentHashes;
		this.stats = stats;
	}

	@Override
	public void run() {
		synchronized(channel) {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					selector.select();
					Set<SelectionKey> keySet= selector.selectedKeys();
					Iterator<SelectionKey> keys = keySet.iterator();
					while(keys.hasNext()) {
						SelectionKey key = keys.next();
						if(key.isReadable()) {
							this.read(key);
						}
						keys.remove();
					}
				} catch (IOException e1) {				
						System.err.println("Connection to server terminated.");
				}
				
			}
		}
	}
	
	private void read(SelectionKey key){
		SocketChannel channel = (SocketChannel)key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(this.SHA1Length);
		int read =0;
		try {
			while(readBuffer.hasRemaining() && read!=-1) {
				read = channel.read(readBuffer);				
			}
			byte[] response = readBuffer.array();			
			synchronized(sentHashes) {
				for(Hash challenge : sentHashes) {
					if (challenge.verify(response)) {
						sentHashes.remove(challenge);
						this.stats.incrementRecieved();
						break;
					}
				}
			}
		}
		catch(IOException e) {
			try {
				System.err.println("Server read code is "+read+". Terminating connection...");
				channel.close();
			} catch (IOException e1) {				
				System.err.println("Connection to server terminated.");
			}
		}		
	}
}

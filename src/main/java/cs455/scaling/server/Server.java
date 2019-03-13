package cs455.scaling.server;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server{	
	int port;
	
	ServerStatistics stats = new ServerStatistics();

	ThreadPoolManager manager;
	
	//default payload length is 8 KB
	int payloadLength = 8000;
	
	
	Selector selector = Selector.open();
	private ServerSocketChannel serverChannel = null;
			
	public Server(int port,	int threadPoolSize, int batchSize, int batchTime) throws IOException {		
		this.port = port;
		this.manager = new ThreadPoolManager(batchSize,batchTime,threadPoolSize,this.stats);
	}
	
	public void Start() {
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);		
			serverChannel.socket().bind(new InetSocketAddress(port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			stats.start();
			
			listen();
		} catch (IOException e) {
			System.err.println("Server could not start on port "+port);
		}
	}

	private void listen() throws IOException {
		while(true) {
			selector.select();
			Set<SelectionKey> keySet= selector.selectedKeys();
			Iterator<SelectionKey> keys = keySet.iterator();
			while(keys.hasNext()) {
				synchronized(keys) {
					SelectionKey key = keys.next();
					if(key.isAcceptable()) {
						SocketChannel client = serverChannel.accept();
						if (client!=null) {
							client.configureBlocking(false);
							synchronized(stats) {
								stats.addConnection(client);
							}
							synchronized(selector) {
								client.register(selector, SelectionKey.OP_READ);
							}	
							System.out.println("Connection accepted: "+client.getLocalAddress());
						}
					}
					if(key.isReadable()) {
						this.read(key);
					}
					keys.remove();
				}
			}			
		
		}
	}
	private void read(SelectionKey key){
		SocketChannel channel = (SocketChannel)key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(this.payloadLength);
		int read =0;
		try {
			while(readBuffer.hasRemaining() && read!=-1) {
				read = channel.read(readBuffer);				
			}
			Task newTask = new Task(channel,this.selector, readBuffer.array());
			this.manager.addTask(newTask);
			
		}
		catch(IOException e) {
			try {
				System.err.println("Client "+channel.getRemoteAddress() +" read code is "+read+". Terminating connection...");
			} catch (IOException channelAddressLookupException) {
				System.err.println("Client read code is "+read+", but could not lookup its remote address. Terminating connection...");
			}
			try {
				channel.close();
			} catch (IOException e1) {				
				System.err.println("Could not terminate the connection gracefully");
			}
		}
		
	}
	public static void main(String[] args) {
		int port = Integer.parseUnsignedInt(args[0]);
		int threadPoolSize = Integer.parseUnsignedInt(args[1]);
		int batchSize = Integer.parseUnsignedInt(args[2]);
		int batchTime = Integer.parseUnsignedInt(args[3]);
		
		try {
			Server server = new Server(port, threadPoolSize, batchSize, batchTime);
			server.Start();
			
		} catch (IOException e) {
			System.err.println("Could not start server on port "+port);
		}
	}
}

package cs455.scaling.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Random;
import cs455.scaling.hash.Hash;

public class Client {
	InetSocketAddress serverAddress;
	int timeBetweenChallenges;	
		
	LinkedList<Hash> sentHashes = new LinkedList<Hash>();
	
	//default payload length is 8 KB
	int payloadLength = 8000;
	
	SenderThread sender;
	Selector selector;
	SocketChannel channel;
	
	ClientStatistics stats = new ClientStatistics();
	
	public Client(String serverHost,int serverPort,int messageRate) {
		this.timeBetweenChallenges = 1000/messageRate;
		this.serverAddress = new InetSocketAddress(serverHost,serverPort);
	}

	public void Start() {
		try {
			this.Start(0);
		} catch (InterruptedException e) {
			System.err.println("Attempting to connect was interrupted. Aborting.");
		}
	}
	private void Start(int attempt) throws InterruptedException {
		int maxConnectionAttempts = 10;
		if(attempt >= maxConnectionAttempts) {
			System.err.println("Tried to unsuccessfully connect to a server for "+maxConnectionAttempts+" times. Aborting.");
			return;
		}
		try {
			this.selector = Selector.open();
			this.channel = SocketChannel.open(serverAddress);
			this.channel.configureBlocking(false);			
			
			this.sender = new SenderThread(this.channel, this.selector);
			this.channel.register(selector, SelectionKey.OP_CONNECT);
			stats.start();
			Thread listener = new Thread(new ResponseHandler(channel, selector,sentHashes,stats));			
			listener.start();
			
			this.startChallenging();
			

		} catch (IOException connectToServerException) {
			System.err.println("Client could not connect to a server at "+this.serverAddress);
			Thread.sleep(5000);
			System.err.println("Trying to connect again...");
			Start(attempt+1);
		}
	}
	private void startChallenging() {
		//int limit =5;
		while (true) {
			nextChallenge();
			stats.incrementSent();
			//limit --;
			try {
				Thread.sleep(this.timeBetweenChallenges);				
			} catch (InterruptedException e) {
				System.err.println("Interrupted when waiting for the next challenge to send");
			}
		}
		
	}
	private void nextChallenge() {
		byte[] challenge = new byte[payloadLength];
		new Random().nextBytes(challenge);
		
		Hash expectedResponse = new Hash(challenge);
		synchronized(this.sentHashes) {
			this.sentHashes.add(expectedResponse);
		}
		
		try {
			sender.sendSomething(challenge);
		} catch (IOException e) {
			System.err.println("Could not send a challenge to server "+this.serverAddress);
		}
	}
	
	public static void main(String[] args) {
		String serverHost = args[0];
		int serverPort = Integer.parseUnsignedInt(args[1]);
		int messageRate = Integer.parseUnsignedInt(args[2]);		
		
		Client client = new Client(serverHost, serverPort, messageRate);
		client.Start();			
	}
}

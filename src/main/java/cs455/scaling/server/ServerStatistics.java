package cs455.scaling.server;

import java.nio.channels.SocketChannel;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class ServerStatistics {
	HashMap<SocketChannel,Integer> knownChannels = new HashMap<SocketChannel,Integer>();
	// every new report generated every 20 seconds
	Thread reportTimer;
	
	public ServerStatistics() {
		reportTimer = new Thread(new OnReportTimer(this));
	}
	public void start() {
		this.reportTimer.start();
	}
	
	public synchronized void taskProcessed(Task task) {
		synchronized(this.knownChannels) {
			int currentCounter = this.knownChannels.get(task.replySocket);
			this.knownChannels.put(task.replySocket, currentCounter+1);
		
		}
	}
	
	public synchronized void addConnection(SocketChannel clientChannel) {
		synchronized(this.knownChannels) {
			if(!this.knownChannels.keySet().contains(clientChannel)) {
				knownChannels.put(clientChannel,0);
			}
		}
	}
	
	private double stdDivThroughput() {		
		synchronized(this.knownChannels) {
			double meanThroughput =meanThroughput();
			double stdDiv = 0;
			
			for(Entry<SocketChannel,Integer> channelData : this.knownChannels.entrySet()) {
				if(channelData.getKey().isOpen())
					stdDiv+=Math.pow(channelData.getValue()-meanThroughput,2);
			}
			
			stdDiv = stdDiv/numberOfConnections();
			
			stdDiv = Math.sqrt(stdDiv);
			
			return stdDiv; 
		}
	}
	
	private double meanThroughput() {	
		synchronized(this.knownChannels) {		
			double messagesProcessed =numberOfTasks();			
			return messagesProcessed/numberOfConnections(); 
		}		
	}

	private int numberOfConnections() {
		int countActive =0;
		synchronized(this.knownChannels) {			
			for(SocketChannel channel : this.knownChannels.keySet()) {
				if(channel.isOpen()) countActive++;
			}
			//TODO: deal with the case when a connection to a client is closed
		}
		return countActive; 
	}
	
	private double numberOfTasks() {
		int messagesProcessed =0;
		synchronized(this.knownChannels) {			
			for(Entry<SocketChannel,Integer> channelData : this.knownChannels.entrySet()) {
				if(channelData.getKey().isOpen())
					messagesProcessed+=channelData.getValue();
			}			
			return messagesProcessed; 
		}
		
	}
	
	private void resetCounters() {;
		synchronized(this.knownChannels) {
			Set<SocketChannel> channels = this.knownChannels.keySet();
			
			synchronized(channels) {		
				for(SocketChannel channel:channels) {								
						if(channel.isOpen())
							this.knownChannels.put(channel, 0);
						else
							this.knownChannels.remove(channel);
				}
			}
		}
	}
	
	private class OnReportTimer implements Runnable{
		ServerStatistics stats;
		long reportTimespan = 20000;
		
		public OnReportTimer(ServerStatistics stats) {
			this.stats = stats;
		}
				
		@Override
		public void run() {
			while(true) {
				String report = "";
				
				report+= "["+DateTimeFormatter.ISO_DATE_TIME.toString() +"] ";
				int numberOfConnections = stats.numberOfConnections();
				
				report+="Server Throughput: "+(double)numberOfTasks()/reportTimespan*1000 +"messages/s, ";
				report+="Active Client Connections: " + numberOfConnections+",";
				
				if(numberOfConnections>0) {
					report+="Mean Per-client Throughput: " + stats.meanThroughput()+"messages/s, ";
					report+="Std. Div. Of Per-client Throughput: " + stats.stdDivThroughput()+"messages/s";
				}
				
				System.out.println(report);
				stats.resetCounters();
				try {
					Thread.sleep(reportTimespan);
				} catch (InterruptedException e) {
					System.err.println("Report printer was interrupted while waiting.");
				}
			}
		} 
	}
}

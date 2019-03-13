package cs455.scaling.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ClientStatistics {
	long sentCounter=0;
	long recievedCounter=0;
	Thread reportPrinter;
	public ClientStatistics() {
		reportPrinter = new Thread(new ReportPrinter(this));
	}

	public void start() {
		this.reportPrinter.start();
	}
	
	public synchronized void incrementSent() {
		sentCounter++;
	}
	public synchronized long getSent() {
		return sentCounter;
	}
	public synchronized void incrementRecieved() {
		recievedCounter++;
	}
	public synchronized long getRecieved() {
		return recievedCounter;
	}
	
	private class ReportPrinter implements Runnable{
		ClientStatistics stats;
		long reportTimespan = 20000;
		
		public ReportPrinter(ClientStatistics stats) {
			this.stats = stats;
		}
				
		@Override
		public void run() {
			while(true) {
				String report = "";
				//adding timestamp
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				report+= "["+dateFormat.format(date) +"] ";
				
				report+= "Total Sent Count: "+stats.getSent()+",";
				report+= "Total Recieved Count: "+stats.getRecieved();
				System.out.println(report);
				try {
					Thread.sleep(reportTimespan);
				} catch (InterruptedException e) {
					System.err.println("Report printer was interrupted while waiting.");
				}
			}
		} 
	}
}

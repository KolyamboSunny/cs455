package cs455.overlay.util;

import java.util.ArrayList;

import cs455.overlay.wireformats.TrafficSummaryResponse;

public class StatisticsCollectorAndDisplay {	
	public static void printExperimentStats(ArrayList<TrafficSummaryResponse> nodeStats) {
		
		int sentCounter=0, recieveCounter=0;
		long sentSummation=0, recieveSummation=0;
		
		
		System.out.format("%7s %15s %15s %20s %20s %15s","","Msg sent","Msg recieved","Sent summation","Recieved Summation","Msg relayed");
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------");
		for(int nodeIndex=0;nodeIndex<nodeStats.size();nodeIndex++){
			TrafficSummaryResponse stat = nodeStats.get(nodeIndex);
	        System.out.format("%7s %15d %15d %20d %20d %15d",
	                "Node"+nodeIndex, stat.sentCounter, stat.recieveCounter, 
	                stat.sentSummation, stat.recieveSummation, 
	                stat.relayCounter);
	        System.out.println();
	        
	        sentCounter+=stat.sentCounter;
	        recieveCounter+=stat.recieveCounter;
			sentSummation+=stat.sentSummation;
			recieveSummation+=stat.recieveSummation;
	    }
		System.out.println("-----------------------------------------------------------------------------");
		System.out.format("%7s %15d %15d %20d %20d",
                "Sum", sentCounter, recieveCounter, 
                sentSummation, recieveSummation);
		System.out.println();
	}
}

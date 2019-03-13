package cs455.scaling.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import cs455.scaling.hash.Hash;

public class WorkerThread extends Thread{
	
	private ThreadPoolManager manager;
	private ServerStatistics stats;
	private LinkedList<Task> toDoList = new LinkedList<Task>();
	
	public WorkerThread(ThreadPoolManager manager,ServerStatistics stats){
		this.manager = manager;		
		this.stats = stats;
	}
	
	public void assignTasks(LinkedList<Task> toDoList) {
		this.toDoList.addAll(toDoList);
		synchronized(this.toDoList) {
			this.toDoList.notify();
		}
	}
	
	private void resolveTask(Task task) {		
			Hash response = new Hash(task.getChallenge());
			synchronized(task.replySocket) {
				if(!task.replySocket.isOpen())
					return;
				
				try {
						ByteBuffer toSend = ByteBuffer.wrap(response.getHash());
						while(toSend.hasRemaining()) {
							task.replySocket.write(toSend);					
						stats.taskProcessed(task);
					}
				}catch(Exception e) {
					System.err.println("Failed to send response to the client: " +e.getLocalizedMessage()+" Terminating connection...");
					try {
						task.replySocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
	}

	@Override
	public void run() {		
		while(!Thread.currentThread().isInterrupted()) {
			synchronized(toDoList) {
				try {
					this.toDoList.wait();
				} catch (InterruptedException e) {
					System.err.println("Worker thread interrupted while awaiting for new tasks.");
				}
			}
			while(!toDoList.isEmpty()) {
				resolveTask(toDoList.pop());
			}
			
			synchronized(manager.freeThreads) {
				manager.freeThreads.add(this);
			}
		}
	}
	
}

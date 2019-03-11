package cs455.scaling.server;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

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
		synchronized(task.replySocket) {
		synchronized(task.selector) {
			Hash response = new Hash(task.getChallenge());
			try {
				task.replySocket.register(task.selector, SelectionKey.OP_WRITE);
			
				task.selector.select();
				Set<SelectionKey> keySet= task.selector.selectedKeys();
				Iterator<SelectionKey> keys = keySet.iterator();
				while(keys.hasNext()) {
					SelectionKey key = keys.next();
					if(key.isWritable()) {
						ByteBuffer toSend = ByteBuffer.wrap(response.getHash());
						
						while(toSend.hasRemaining()) {
							task.replySocket.write(toSend);
							
						}
						stats.taskProcessed(task);
					}
		
				}
				task.replySocket.register(task.selector, SelectionKey.OP_READ);
			}catch(Exception e) {
				System.err.println("Failed to send response to the client" +e.getLocalizedMessage());
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

package cs455.scaling.server;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadPoolManager{
	LinkedList<Task> tasks = new LinkedList<Task>();
	LinkedList<WorkerThread> freeThreads = new LinkedList<WorkerThread>(); 
	
	int batchSize;
	int batchTime;

	Timer batchTimer = new Timer();
	
	public ThreadPoolManager( int batchSize, int batchTime,	int threadPoolSize, ServerStatistics stats) {
		this.batchSize = batchSize;
		this.batchTime = batchTime;
		for(int workerThreadId=0;workerThreadId<threadPoolSize;workerThreadId++) {
			WorkerThread newWorker = new WorkerThread(this,stats);			
			newWorker.start();
			freeThreads.add(newWorker);
		}
	}
	
	public void addTask(Task newTask) {
		synchronized(this.batchTimer) {
			batchTimer.schedule(new OnBatchTimer(this), batchTime);
			synchronized(tasks) {
				tasks.add(newTask);
			
				if (tasks.size()>=batchSize) {
					batchTimer.purge();
					assignWorker(batchSize);
				}
			}
		}
	}
	
	private void assignWorker(int numberOfTasks) {
		synchronized(freeThreads) {
		synchronized(tasks) {
			while(!freeThreads.isEmpty() && tasks.size()>=numberOfTasks) {
				LinkedList<Task> toDoList = new LinkedList<Task>();
				for(int i=0;i<numberOfTasks;i++)
					toDoList.add(tasks.pop());
				WorkerThread freeWorker = freeThreads.pop();
				freeWorker.assignTasks(toDoList);				
			}
		}
		}
	}
	
	private class OnBatchTimer extends TimerTask{
		ThreadPoolManager manager;
		
		public OnBatchTimer(ThreadPoolManager manager) {
			this.manager = manager;
		}
		@Override
		public void run() {
			synchronized(tasks) {
			synchronized(freeThreads) {
				if(!freeThreads.isEmpty() && !tasks.isEmpty())
					manager.assignWorker(tasks.size());
			}
			}
		} 

	}
}

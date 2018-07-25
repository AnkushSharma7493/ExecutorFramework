package com.concurency.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool {
	private static AtomicInteger ThreadPoolCount=new AtomicInteger(0);
	private ConcurrentLinkedQueue<Runnable> runnables;
	private AtomicBoolean execute;
	private List<ExecutorThread> Threads;

	private ThreadPool(int systemprocessors) {
		this.ThreadPoolCount.incrementAndGet();
		this.runnables = new ConcurrentLinkedQueue<>();
		this.execute = new AtomicBoolean(true);
		this.Threads=new ArrayList<>();
		for (int i = 0; i < systemprocessors; i++) {
			ExecutorThread thread = new ExecutorThread("MyThread - " + i, runnables, execute);
			thread.start();
			this.runnables.add(thread);
		}
	}

	public void submit(Runnable runnable) {
		if (this.execute.get()) {
			runnables.add(runnable);
		} else {
			throw new IllegalStateException();
		}
	}

	public void stop() {
		this.execute.set(false);
	}

	public static ThreadPool getinstance() {
		return new ThreadPool(Runtime.getRuntime().availableProcessors());
	}

	// This thread pool will stop only if all jobs have been completed present in the queue.
	private class ExecutorThread extends Thread {
		ConcurrentLinkedQueue<Runnable> runnables;
		AtomicBoolean execute;
		public ExecutorThread(String name, ConcurrentLinkedQueue<Runnable> runnables, AtomicBoolean execute) {
			super(name);
			this.execute = execute;
			this.runnables = runnables;
		}

		public void run() {
			try {
				while (execute.get() || !runnables.isEmpty()) {
					Runnable runnable;
					while ((runnable = runnables.poll()) != null) {
						runnable.run();
					}

					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

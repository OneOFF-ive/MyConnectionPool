package com.five.pool.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThreadPool {

    private final BlockingQueue<Runnable> taskQueue;
    private final WorkerThread[] workerThreads;
    private volatile boolean isShutdown;

    public MyThreadPool(int poolSize) {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workerThreads = new WorkerThread[poolSize];
        this.isShutdown = false;

        for (int i = 0; i < poolSize; i++) {
            workerThreads[i] = new WorkerThread();
            workerThreads[i].start();
        }
    }

    public void execute(Runnable task) throws InterruptedException {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is already shutdown");
        }
        taskQueue.put(task);
    }

    public void shutdown() {
        isShutdown = true;
        for (WorkerThread thread : workerThreads) {
            thread.interrupt();
        }
    }

    private class WorkerThread extends Thread {
        public void run() {
            while (!isInterrupted()) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }
}

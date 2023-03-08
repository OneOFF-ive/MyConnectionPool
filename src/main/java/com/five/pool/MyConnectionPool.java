package com.five.pool;

import com.five.pool.thread.MyThreadPool;

import java.util.*;
import java.util.concurrent.CountDownLatch;


public class MyConnectionPool<T> {

    private final List<T> connectionPool;
    private final PoolConfig poolConfig;
    private final ConnectionFactory<T> connectionFactory;
    private final Map<T, Long> connBuildTime;
    private final Object lock;
    private final Timer timer;
    private final MyThreadPool threadPool;

    public MyConnectionPool(PoolConfig poolConfig, ConnectionFactory<T> connectionFactory) {
        this.connectionPool = Collections.synchronizedList(new ArrayList<>(poolConfig.maxSize));
        this.poolConfig = poolConfig;
        this.connectionFactory = connectionFactory;
        connBuildTime = Collections.synchronizedMap(new HashMap<>());
        lock = new Object();
        timer = new Timer();
        threadPool = new MyThreadPool(poolConfig.maxSize);
        init();
    }

    public void init() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("start heartBeat");
                connectionPool.forEach((conn) -> {
                    if (!isConnectionValid(conn)) {
                        connBuildTime.remove(conn);
                        conn = connectionFactory.buildConnection();
                        connBuildTime.put(conn, System.currentTimeMillis());
                    }
                });
                System.out.println("finish heartBeat");
            }
        }, poolConfig.maxIdleTime, poolConfig.maxIdleTime);

        try {
            CountDownLatch countDownLatch = new CountDownLatch(poolConfig.maxSize);
            for (int i = 0; i < poolConfig.maxSize; i++) {
                threadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getId() + " start create connection");
                    var conn = connectionFactory.buildConnection();
                    connBuildTime.put(conn, System.currentTimeMillis());
                    connectionPool.add(conn);
                    countDownLatch.countDown();
                    System.out.println(Thread.currentThread().getId() + " finish create connection");
                });
            }
            countDownLatch.await();
            threadPool.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public T getConnection() {
        synchronized (lock) {

            while (connectionPool.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            T conn = connectionPool.remove(connectionPool.size() - 1);

            if (poolConfig.checkAlways && !isConnectionValid(conn)) {
                connBuildTime.remove(conn);
                conn = connectionFactory.buildConnection();
                connBuildTime.put(conn, System.currentTimeMillis());
            }

            return conn;
        }
    }

    public boolean releaseConnection(T conn) {
        if (poolConfig.checkAlways && !isConnectionValid(conn)) {
            synchronized (lock) {
                if (connBuildTime.remove(conn) != null) {
                    conn = connectionFactory.buildConnection();
                    connBuildTime.put(conn, System.currentTimeMillis());
                    connectionPool.add(conn);

                    try {
                        lock.notify();
                    } catch (IllegalMonitorStateException ignored) {

                    }
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized int available() {
        return connectionPool.size();
    }

    public boolean isConnTimeOut(T conn) {
        var buildTime = connBuildTime.get(conn);
        if (buildTime != null) {
            return System.currentTimeMillis() - buildTime > poolConfig.maxIdleTime;
        }
        return false;
    }

    private boolean isConnectionValid(T conn) {
        if (poolConfig.maxIdleTime > 0) {
            return (!poolConfig.checkTimeOut || !isConnTimeOut(conn))
                    && (!poolConfig.validateConnection || connectionFactory.validateConnection(conn));
        }
        return false;
    }

    public void shutdown() {
        timer.cancel();
        connectionPool.clear();
    }

}
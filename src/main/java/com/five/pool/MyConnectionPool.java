package com.five.pool;

import java.util.*;


public class MyConnectionPool<T> {

    private final List<T> connectionPool;
    private final PoolConfig poolConfig;
    private final ConnectionFactory<T> connectionFactory;
    private final Map<T, Long> connBuildTime;
    private final Object lock;
    private final Timer timer;

    public MyConnectionPool(PoolConfig poolConfig, ConnectionFactory<T> connectionFactory) {
        this.connectionPool = new ArrayList<>(poolConfig.maxSize);
        this.poolConfig = poolConfig;
        this.connectionFactory = connectionFactory;
        connBuildTime = new HashMap<>();
        lock = new Object();
        timer = new Timer();
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

        for (int i = 0; i < poolConfig.maxSize; i++) {
            var conn = connectionFactory.buildConnection();
            connBuildTime.put(conn, System.currentTimeMillis());
            connectionPool.add(conn);
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

    public void releaseConnection(T conn) {
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
                }
            }
        }
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

    void shutdown() {
        timer.cancel();
        connectionPool.clear();
    }

}
package com.five.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class MyConnectionPool<T> {

    private final List<T> connectionPool;
    private final List<T> usedConnections;
    private final PoolConfig poolConfig;
    private final ConnectionFactory<T> connectionFactory;

    private final Object lock = new Object();

    public MyConnectionPool(PoolConfig poolConfig, ConnectionFactory<T> connectionFactory) {
        this.connectionPool = new ArrayList<>(poolConfig.maxSize);
        this.usedConnections = new ArrayList<>(poolConfig.maxSize);
        this.poolConfig = poolConfig;
        this.connectionFactory = connectionFactory;

        init();
    }

    private void init() {
        for (int i = 0; i < poolConfig.initSize; i++) {
            var conn = connectionFactory.buildConnection();
            connectionPool.add(conn);
        }
    }

    @SuppressWarnings("WaitWhileHoldingTwoLocks")
    public synchronized T getConnection() {
        synchronized (lock) {
            if (!connectionPool.isEmpty()) {
                var conn = connectionPool.remove(connectionPool.size() - 1);
                usedConnections.add(conn);
                return conn;
            } else if (usedConnections.size() < poolConfig.maxSize) {
                var conn = connectionFactory.buildConnection();
                usedConnections.add(conn);
                return conn;
            } else {
                while (connectionPool.isEmpty()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                var conn = connectionPool.remove(connectionPool.size() - 1);
                usedConnections.add(conn);
                return conn;
            }
        }
    }

    public synchronized void release(T obj) {
        if (usedConnections.remove(obj)) {
            connectionPool.add(obj);
            try {
                lock.notify();
            } catch (IllegalMonitorStateException ignored) {
            }
        }
    }

    public synchronized int available() {
        return connectionPool.size();
    }

}
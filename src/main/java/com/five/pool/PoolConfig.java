package com.five.pool;

public class PoolConfig {
    public int maxSize;
    public long maxIdleTime;
    public boolean heartBeat;
    public boolean checkTimeOut;
    public boolean validateConnection;
    public boolean checkAlways;

    public PoolConfig() {
        this.maxSize = 5;
        this.maxIdleTime = 600000;
        heartBeat = true;
        checkTimeOut = true;
        validateConnection = true;
        checkAlways = true;
    }

    public PoolConfig(int maxSize, long maxIdleTime, boolean heartBeat, boolean checkTimeOut, boolean validateConnection, boolean checkAlways) {
        this.maxSize = maxSize;
        this.maxIdleTime = maxIdleTime;
        this.heartBeat = heartBeat;
        this.checkTimeOut = checkTimeOut;
        this.validateConnection = validateConnection;
        this.checkAlways = checkAlways;
    }

    public PoolConfig setMaxSize(int n) {
        this.maxSize = n;
        return this;
    }

    public PoolConfig setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    public PoolConfig setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
        return this;
    }

    public PoolConfig setCheckTimeOut(boolean checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
        return this;
    }

    public PoolConfig setValidateConnection(boolean validateConnection) {
        this.validateConnection = validateConnection;
        return this;
    }

    public PoolConfig setCheckAlways(boolean checkAlways) {
        this.checkAlways = checkAlways;
        return this;
    }
}

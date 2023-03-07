package com.five.pool;

public class PoolConfig {
    public int maxSize;
    public int initSize;


    public PoolConfig(int maxSize, int initSize) {
        this.maxSize = maxSize;
        this.initSize = initSize;
    }
}

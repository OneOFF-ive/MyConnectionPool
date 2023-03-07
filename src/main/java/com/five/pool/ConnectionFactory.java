package com.five.pool;

public interface ConnectionFactory<T> {
    T buildConnection();
}

# MyConnectionPool
## 手动实现一个连接池  
ConnectionFactory 接口用于生成连接和检测连接状态，提高复用性  
PoolConfig 用于配置连接池信息  
```java
public class PoolConfig {
    //连接池最大连接数
    public int maxSize;
    //连接最大空闲时间
    public long maxIdleTime;
    //连接池是否心跳检测
    public boolean heartBeat;
    //连接池是否检测连接超时
    public boolean checkTimeOut;
    //连接池是否检测连接状态
    public boolean validateConnection;
    //每次从连接池中获取连接或归还连接时是否检测连接可用性
    public boolean checkAlways;
}
```  
连接池的使用方法：  
```java
//构造方法
public MyConnectionPool(PoolConfig poolConfig, ConnectionFactory<T> connectionFactory)
//获取连接
public T getConnection()
//返还连接
public void releaseConnection(T conn)
//关闭连接池
void shutdown()
//初始化连接池，连接池初始化时自动调用，通常不需要手动调用，只有在关闭连接池后才需要使用
public void init()
```  

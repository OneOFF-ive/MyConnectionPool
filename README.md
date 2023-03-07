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
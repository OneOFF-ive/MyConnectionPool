package com.five.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        var myConnectionPool = new MyConnectionPool<Connection>(new PoolConfig(), new ConnectionFactory<Connection>() {
            @Override
            public Connection buildConnection() {
                try {
                    return DriverManager.getConnection("jdbc:mysql://139.196.48.244:4407/BookLibrary", "wu", "Wzh913000");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean validateConnection(Connection connection) {
                try {
                    return connection.isValid(1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        List<Connection> list = new ArrayList<>();
        for (int i = 0;  i < 5; i++) {
            list.add(myConnectionPool.getConnection());
        }
        for (var conn : list) {
            myConnectionPool.release(conn);
            System.out.println(conn);
        }
        while (true) {

        }
    }
}

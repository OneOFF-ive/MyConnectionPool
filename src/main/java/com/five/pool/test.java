package com.five.pool;



public class test {
    public static void main(String[] args) {}
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        var myConnectionPool = new MyConnectionPool<Connection>(new PoolConfig(), new ConnectionFactory<Connection>() {
//            @Override
//            public Connection buildConnection() {
//                try {
//                    return DriverManager.getConnection("jdbc:mysql://139.196.48.244:4407/BookLibrary", "wu", "Wzh913000");
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            @Override
//            public boolean validateConnection(Connection connection) {
//                try {
//                    return connection.isValid(1);
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        List<Connection> list = new ArrayList<>();
//        for (int i = 0;  i < 5; i++) {
//            list.add(myConnectionPool.getConnection());
//        }
//        for (var conn : list) {
//            myConnectionPool.releaseConnection(conn);
//            System.out.println(conn);
//        }
//        while (true) {
//
//        }
//    }
}

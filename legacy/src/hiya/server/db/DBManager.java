package hiya.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接管理器。负责加载JDBC驱动并提供数据库连接。
 * 每次调用getConnection都返回一个新的Connection，供DAO层使用。
 */
public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/hiya_db?useSSL=false&serverTimezone=Asia/Shanghai&useServerPrepStmts=true"; // 数据库连接URL
    private static final String USER = "root";      // 数据库用户名
    private static final String PASSWORD = "123456"; // 数据库密码

    static {
        try {
            // 加载MySQL JDBC驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // 驱动加载失败，打印异常
            e.printStackTrace();
            throw new RuntimeException("无法加载MySQL JDBC驱动", e);
        }
    }

    /**
     * 获取一个新的数据库连接。
     * @return 数据库Connection对象
     * @throws SQLException 如果连接失败
     */
    public static Connection getConnection() throws SQLException {
        // 通过DriverManager获取数据库连接
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
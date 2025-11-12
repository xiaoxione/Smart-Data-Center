package com.briup.env.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//JDBC工具类
public class JdbcUtils {
    //数据源对象
    private static DataSource dataSource;

    //工具类加载时读取配置文件，获取数据源对象
    static {
        try {
            Properties properties = new Properties();
            properties.load(JdbcUtils.class.getClassLoader().getResourceAsStream("druid.properties"));
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //提供私有无参构造器
    private JdbcUtils() {}

    //从数据库连接池中获取连接对象
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    //关闭资源
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //关闭资源方法重载
    public static void close(Statement stmt, Connection conn) {
        close(null, stmt, conn);
    }
}


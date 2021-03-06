package com.cw.kafka.consumer.mysql;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * JDBC操作工具类, 提供注册驱动, 连接, 发送器, 动态绑定参数, 关闭资源等方法
 * jdbc连接参数的提取, 使用Properties进行优化(软编码)
 *
 * @author 陈小哥cw
 * @date 2020/6/19 19:41
 */
public class DBUtils {

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    static {
        // 借助静态代码块保证配置文件只读取一次就行
        // 创建Properties对象
        Properties prop = new Properties();
        try {
            // 加载配置文件, 调用load()方法
            // 类加载器加载资源时, 去固定的类路径下查找资源, 因此, 资源文件必须放到src目录才行
            prop.load(DBUtils.class.getClassLoader().getResourceAsStream("db.properties"));
            // 从配置文件中获取数据为成员变量赋值
            driver = prop.getProperty("db.driver").trim();
            url = prop.getProperty("db.url").trim();
            user = prop.getProperty("db.user").trim();
            password = prop.getProperty("db.password").trim();
            // 加载驱动
            Class.forName(driver);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态绑定参数
     *
     * @param pstmt
     * @param params
     */
    public static void bindParam(PreparedStatement pstmt, Object... params) {
        try {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 预处理发送器
     *
     * @param conn
     * @param sql
     * @return
     */
    public static PreparedStatement getPstmt(Connection conn, String sql) {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pstmt;
    }

    /**
     * 获取发送器的方法
     *
     * @param conn
     * @return
     */
    public static Statement getStmt(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 获取数据库连接的方法
     *
     * @return
     */
    public static Connection getConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 获取特定消费者组，主题，分区下的偏移量
     *
     * @return offset
     */
    public static long queryOffset(String sql, Object... params) {
        Connection conn = getConn();
        long offset = 0;
        PreparedStatement preparedStatement = getPstmt(conn, sql);
        bindParam(preparedStatement, params);

        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                offset = resultSet.getLong("sub_topic_partition_offset");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet, preparedStatement, conn);
        }

        return offset;
    }

    /**
     * 根据特定消费者组，主题，分区，更新偏移量
     *
     * @param offset
     */
    public static void update(String sql, Offset offset) {
        Connection conn = getConn();
        PreparedStatement preparedStatement = getPstmt(conn, sql);

        bindParam(preparedStatement,
                offset.getConsumer_group(),
                offset.getSub_topic(),
                offset.getSub_topic_partition_id(),
                offset.getSub_topic_partition_offset(),
                offset.getTimestamp()
        );

        try {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(null, preparedStatement, conn);
        }

    }


    /**
     * 统一关闭资源
     *
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}

package net.ys.db;

import java.io.IOException;
import java.sql.*;

/**
 * 根据mysql数据库直接生成sql语句
 * User: NMY
 * Date: 17-5-10
 */
public class GenerateSql {
    static Connection connection = null;
    static Statement statement = null;
    static ResultSet rs = null;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(BeanMain.URL, BeanMain.USER_NAME, BeanMain.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void select(String dbName, String tableName) throws SQLException, IOException {
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='%s'";
        String columnName;
        statement = connection.createStatement();
        rs = statement.executeQuery(String.format(sql, tableName, dbName));
        StringBuffer sb = new StringBuffer("SELECT ");
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME").toLowerCase();
            sb.append("`" + columnName + "`, ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" FROM `" + tableName + "` WHERE 1 = 1");
        System.out.println(sb.toString());
    }

    public static void update(String dbName, String tableName) throws SQLException, IOException {
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='%s'";
        String columnName;
        statement = connection.createStatement();
        rs = statement.executeQuery(String.format(sql, tableName, dbName));
        StringBuffer sb = new StringBuffer("UPDATE `").append(tableName).append("` SET ");
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME").toLowerCase();
            if ("id".equals(columnName)) {
                continue;
            }
            sb.append("`" + columnName + "` = ?, ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" WHERE 1 = 1");
        System.out.println(sb.toString());
    }

    public static void insert(String dbName, String tableName) throws SQLException, IOException {
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='%s'";
        String columnName;
        statement = connection.createStatement();
        rs = statement.executeQuery(String.format(sql, tableName, dbName));
        StringBuffer sb = new StringBuffer("INSERT INTO `").append(tableName).append("` (");
        int size = 0;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME").toLowerCase();
            if ("id".equals(columnName)) {
                continue;
            }
            sb.append("`" + columnName + "`, ");
            size++;
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES (").append(genMark(size)).append(") WHERE 1 = 1");
        System.out.println(sb.toString());
    }

    /**
     * 生成问号字符串
     *
     * @param size
     * @return
     */
    public static String genMark(int size) {
        StringBuffer sb = new StringBuffer("?");
        for (int i = 1; i < size; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
}

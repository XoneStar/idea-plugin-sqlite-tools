package com.plugin.sqlite.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuji on 2017/11/5.
 */
public class DButils {

    //获得数据库表列表
    public static List<String> getTables(String db_path) {
        List<String> tables = new ArrayList<>();

        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");

            String url = String.format("jdbc:sqlite:%s", db_path);
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            while (rs.next()) {
                tables.add(rs.getString("name"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e1) {
                System.err.println(e1.getMessage());
            }
        }
        return tables;
    }

    //获取表的所有字段名
    public static List<String> getTableNames(String db_path, String table) {
        List<String> cols = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s limit 0,1", table);
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");

            String url = String.format("jdbc:sqlite:%s", db_path);
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                cols.add(rsmd.getColumnName(i));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e1) {
                System.err.println(e1.getMessage());
            }
        }
        return cols;
    }
}

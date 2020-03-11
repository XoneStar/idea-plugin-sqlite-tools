package com.plugin.sqlite;

import com.opslab.util.FileUtil;
import org.apache.velocity.util.StringUtils;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuji on 2018/11/22.
 */
public class GenerateClassUtil {

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

    //生成javaBean
    public static void generateBean(String dbFilePath, String packageName,
                                    String sourcePath, List<String> list) {
        if (list != null && list.size() > 0) {
            for (String table : list) {
                StringBuilder class_content = new StringBuilder();
                StringBuilder method_content = new StringBuilder();
                List<String> cols = getTableNamesByName(dbFilePath, table);
                cols.forEach(col -> {
                    class_content.append(String.format("\tprivate String %s;\n", col));
                    method_content.append(String.format("\n\tpublic String get%s(){\n" +
                                    "\t\treturn %s;\n\t}\n",
                            StringUtils.capitalizeFirstLetter(col), col))
                            .append(String.format("\n\tpublic void set%s(String %s){\n" +
                                            "\t\tthis.%s = %s;\n\t}\n",
                                    StringUtils.capitalizeFirstLetter(col), col, col, col));
                });
                String javaStr = String.format("package %s;\n" +
                        "\nimport java.lang.String;\n" +
                        "\npublic class %s {\n" +
                        "%s%s}", packageName, table, class_content, method_content);
                FileUtil.write(new File(sourcePath + File.separator + table + ".java"),
                        javaStr, "utf-8");
            }
        }
    }

    //生成javaBean
    public static void generateBean(String dbFilePath, String packageName,
                                     String sourcePath, String sql) {
        if (dbFilePath == null || sql == null)
            return;
        StringBuilder class_content = new StringBuilder();
        StringBuilder method_content = new StringBuilder();
        List<String> cols = getTableNamesBySql(dbFilePath, sql);
        String pattern = ".*(?:FROM|from) (.*?) ";
        String table = "Temp";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(sql);
        if (m.find()) {
            table = m.group(1);
        }
        for (String col : cols) {
            class_content.append(String.format("\tprivate String %s;\n", col));
            method_content.append(String.format("\n\tpublic String get%s(){\n" +
                            "\t\treturn %s;\n\t}\n",
                    StringUtils.capitalizeFirstLetter(col), col))
                    .append(String.format("\n\tpublic void set%s(String %s){\n" +
                                    "\t\tthis.%s = %s;\n\t}\n",
                            StringUtils.capitalizeFirstLetter(col), col, col, col));
        }
        String javaStr = String.format("package %s;\n" +
                "\nimport java.lang.String;\n" +
                "\npublic class %s {\n" +
                "%s%s}", packageName, table, class_content, method_content);
        FileUtil.write(new File(sourcePath + File.separator + table + ".java"),
                javaStr, "utf-8");
    }

    //获取表的所有字段名
    private static List<String> getTableNamesBySql(String db_path, String sql) {
        List<String> cols = new ArrayList<>();
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

    //获取表的所有字段名
    private static List<String> getTableNamesByName(String db_path, String table) {
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

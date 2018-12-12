package net.ys;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据mysql数据库直接生成BeanMapper.java文件
 * User: NMY
 * Date: 17-5-10
 */
public class GenerateMapper {
    static Connection connection = null;
    static Statement statement = null;
    static ResultSet rs = null;
    static String oneEnter = "\r\n";
    static String twoEnter = "\r\n\r\n";
    static String oneTabStr = "\t";
    static String twoTabStr = "\t\t";

    static {
        try {
            File file = new File(BeanMain.MAPPER_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(BeanMain.URL, BeanMain.USER_NAME, BeanMain.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateBean(String dbName) throws SQLException, IOException {
        List<String> tables = getTables(dbName);

        if (tables.size() > 0) {
            String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='" + dbName + "'";
            String columnDb;//数据库中字段名称
            String columnName;
            String columnClassName;
            String beanName;
            String classObject;
            String className;
            FileWriter fileWriter;
            String attributeType = "";
            for (String table : tables) {
                beanName = camelFormat(table, true);
                classObject = camelFormat(table, false);
                className = beanName + "Mapper";

                fileWriter = new FileWriter(BeanMain.MAPPER_PATH + className + ".java");
                fileWriter.write("import net.ys.bean." + beanName + ";" + oneEnter);
                fileWriter.write("import org.springframework.jdbc.core.RowMapper;" + oneEnter);
                fileWriter.write("import java.sql.ResultSet;" + oneEnter);
                fileWriter.write("import java.sql.SQLException;" + twoEnter);

                fileWriter.write("public class " + className + " implements RowMapper<" + beanName + "> {" + oneEnter);
                fileWriter.write(oneTabStr + "@Override" + oneEnter);
                fileWriter.write(oneTabStr + "public " + beanName + " mapRow(ResultSet resultSet, int i) throws SQLException {" + oneEnter);
                fileWriter.write(twoTabStr + beanName + " " + classObject + " = new " + beanName + "();" + oneEnter);

                statement = connection.createStatement();
                rs = statement.executeQuery(String.format(sql, table));
                while (rs.next()) {
                    columnDb = rs.getString("COLUMN_NAME").toLowerCase();
                    columnName = camelFormat(rs.getString("COLUMN_NAME"), true);
                    columnClassName = rs.getString("DATA_TYPE");

                    if ("varchar".equals(columnClassName) || "mediumtext".equals(columnClassName)) {
                        attributeType = "String";
                    } else if ("int".equals(columnClassName)) {
                        attributeType = "Int";
                    } else if ("bigint".equals(columnClassName)) {
                        attributeType = "Long";
                    } else if ("decimal".equals(columnClassName)) {
                        attributeType = "BigDecimal";
                    }
                    fileWriter.write(twoTabStr + classObject + ".set" + columnName + "(resultSet.get" + attributeType + "(\"" + columnDb + "\"));" + oneEnter);
                }
                fileWriter.write(twoTabStr + "return " + classObject + ";" + oneEnter);
                fileWriter.write(oneTabStr + "}" + oneEnter);
                fileWriter.write("}");
                fileWriter.close();
            }
        }
    }

    /**
     * 获取表名
     *
     * @param dbName
     * @return
     * @throws java.sql.SQLException
     */
    public static List<String> getTables(String dbName) throws SQLException {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA. TABLES WHERE TABLE_SCHEMA = '" + dbName + "'";
        statement = connection.createStatement();
        rs = statement.executeQuery(sql);
        List<String> tables = new ArrayList<String>();
        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME"));
        }
        return tables;
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String firstToUpperCase(String str) {
        str = str.toLowerCase();
        String firstLetter = str.charAt(0) + "";
        str = firstLetter.toUpperCase() + str.substring(1);
        return str;
    }

    /**
     * 驼峰标识
     *
     * @param resource
     * @param isClass  是否为类名
     * @return
     */
    public static String camelFormat(String resource, boolean isClass) {
        if (resource != null && resource.trim().length() > 0) {
            String[] strings = resource.split("_");
            if (strings.length > 1) {
                StringBuffer sb = new StringBuffer();
                if (isClass) {
                    sb.append(firstToUpperCase(strings[0]));
                } else {
                    sb.append(strings[0].toLowerCase());
                }
                for (int i = 1; i < strings.length; i++) {
                    sb.append(firstToUpperCase(strings[i]));
                }
                return sb.toString();
            } else {
                if (isClass) {
                    return firstToUpperCase(strings[0]);
                } else {
                    return strings[0].toLowerCase();
                }
            }
        }
        return "";
    }
}

package net.ys;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateBean {
    static Connection connection = null;
    static Statement statement = null;
    static ResultSet rs = null;
    static String oneEnter = "\r\n";
    static String twoEnter = "\r\n\r\n";
    static String oneTabStr = "\t";
    static String twoTabStr = "\t\t";


    static {
        try {
            File file = new File(BeanMain.BEAN_PATH);
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
            String columnName;
            String columnClassName;
            String columnComment;
            String fileName;
            FileWriter fileWriter;
            String columnField;
            String columnClass;
            String attributeType = "";
            for (String table : tables) {
                fileName = camelFormat(table, true);

                fileWriter = new FileWriter(BeanMain.BEAN_PATH + fileName + ".java");

                rs = statement.executeQuery("SELECT COUNT(COLUMN_TYPE) AS c FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = '" + dbName + "' AND TABLE_NAME = '" + table + "' AND DATA_TYPE = 'decimal'");
                if (rs.first()) {
                    if (rs.getInt("c") > 0) {
                        fileWriter.write("import java.math.BigDecimal;" + twoEnter);
                    }
                }
                rs = statement.executeQuery("SELECT TABLE_COMMENT FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '" + dbName + "' AND TABLE_NAME = '" + table + "';");
                String tableComment = "";
                if (rs.first()) {
                    tableComment = rs.getString("TABLE_COMMENT");
                }
                fileWriter.write("import java.io.Serializable;" + twoEnter);
                fileWriter.write("/**" + oneEnter);
                fileWriter.write("* " + tableComment + oneEnter);
                fileWriter.write("*/" + oneEnter);
                fileWriter.write("public class " + fileName + " implements Serializable {" + twoEnter);

                statement = connection.createStatement();
                rs = statement.executeQuery(String.format(sql, table));
                while (rs.next()) {
                    columnName = camelFormat(rs.getString("COLUMN_NAME"), false);
                    columnClassName = rs.getString("DATA_TYPE");
                    columnComment = rs.getString("COLUMN_COMMENT");

                    if ("varchar".equals(columnClassName) || "mediumtext".equals(columnClassName)) {
                        attributeType = "String";
                    } else if ("int".equals(columnClassName)) {
                        attributeType = "int";
                    } else if ("bigint".equals(columnClassName)) {
                        attributeType = "long";
                    } else if ("decimal".equals(columnClassName)) {
                        attributeType = "BigDecimal";
                    }
                    fileWriter.write(oneTabStr + "private " + attributeType + " " + columnName + ";\t//" + columnComment + twoEnter);
                }

                rs = statement.executeQuery(String.format(sql, table));

                while (rs.next()) {
                    columnName = rs.getString("COLUMN_NAME").toLowerCase();
                    columnClassName = rs.getString("DATA_TYPE");
                    columnField = camelFormat(columnName, false);
                    columnClass = camelFormat(columnName, true);

                    if ("varchar".equals(columnClassName) || "mediumtext".equals(columnClassName)) {
                        attributeType = "String";
                    } else if ("int".equals(columnClassName)) {
                        attributeType = "int";
                    } else if ("bigint".equals(columnClassName)) {
                        attributeType = "long";
                    } else if ("decimal".equals(columnClassName)) {
                        attributeType = "BigDecimal";
                    }

                    fileWriter.write(oneTabStr + "public void set" + columnClass + "(" + attributeType + " " + columnField + ") {" + oneEnter);
                    fileWriter.write(twoTabStr + "this." + columnField + " = " + columnField + ";" + oneEnter + "    }" + twoEnter);
                    fileWriter.write(oneTabStr + "public " + attributeType + " get" + columnClass + "() {" + oneEnter);
                    fileWriter.write(twoTabStr + "return this." + columnField + ";" + oneEnter + "    }" + twoEnter);
                }
                fileWriter.write("}");
                fileWriter.close();
            }
        }
    }

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

    public static String firstToUpperCase(String str) {
        str = str.toLowerCase();
        String firstLetter = str.charAt(0) + "";
        str = firstLetter.toUpperCase() + str.substring(1);
        return str;
    }

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

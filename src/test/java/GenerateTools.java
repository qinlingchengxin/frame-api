import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateTools {

    public static void main(String[] args) throws Exception {
        generateBean();
        generateMapper();
        generateRespMapper();
        select();
        update();
        insert();

        statement.close();
        connection.close();
    }

    static ResultSet rs = null;
    static String oneEnter = "\r\n";
    static String twoEnter = "\r\n\r\n";
    static String oneTabStr = "\t";
    static String twoTabStr = "\t\t";

    static final String DB_NAME = "sign";
    static final String TABLE_NAME = "sys_admin";
    static final String URL = "jdbc:mysql://10.10.10.51:3306/" + DB_NAME;
    static final String USER_NAME = "root";
    static final String PASSWORD = "zhulong123321";
    static final String BEAN_PATH = "D:/bean/";
    static final String MAPPER_PATH = "D:/bean/mapper/";
    static final String RESP_MAPPER_PATH = "D:/bean/ResponseMapper/";
    static Connection connection = null;
    static Statement statement = null;

    static {
        try {
            File file = new File(BEAN_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(MAPPER_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(RESP_MAPPER_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateBean() throws SQLException, IOException {
        List<String> tables = getTables();

        if (tables.size() > 0) {
            String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='" + DB_NAME + "'";
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

                fileWriter = new FileWriter(BEAN_PATH + fileName + ".java");

                rs = statement.executeQuery("SELECT COUNT(COLUMN_TYPE) AS c FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = '" + DB_NAME + "' AND TABLE_NAME = '" + table + "' AND DATA_TYPE = 'decimal'");
                if (rs.first()) {
                    if (rs.getInt("c") > 0) {
                        fileWriter.write("import java.math.BigDecimal;" + twoEnter);
                    }
                }
                rs = statement.executeQuery("SELECT TABLE_COMMENT FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '" + DB_NAME + "' AND TABLE_NAME = '" + table + "';");
                String tableComment = "";
                if (rs.first()) {
                    tableComment = rs.getString("TABLE_COMMENT");
                }
                fileWriter.write("import java.io.Serializable;" + twoEnter);
                fileWriter.write("/**" + oneEnter);
                fileWriter.write("* " + tableComment + oneEnter);
                fileWriter.write("*/" + oneEnter);
                fileWriter.write("public class " + fileName + " implements Serializable {" + twoEnter);

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

    public static void generateRespMapper() throws SQLException, IOException {
        List<String> tables = getTables();

        if (tables.size() > 0) {
            String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='" + DB_NAME + "'";
            String columnName;
            String columnClassName;
            String columnComment;
            String fileName;
            FileWriter fileWriter;
            String columnClass;
            String attributeType = "";
            for (String table : tables) {
                fileName = camelFormat(table, true);

                fileWriter = new FileWriter(RESP_MAPPER_PATH + fileName + "Response.java");

                fileWriter.write("import io.swagger.annotations.ApiModelProperty;" + twoEnter);
                fileWriter.write("public class " + fileName + "Response {" + twoEnter);

                rs = statement.executeQuery(String.format(sql, table));
                while (rs.next()) {
                    columnName = rs.getString("COLUMN_NAME").toLowerCase();
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
                    fileWriter.write(oneTabStr + "@ApiModelProperty(value = \"" + columnComment + "\")" + oneEnter);
                    fileWriter.write(oneTabStr + "private " + attributeType + " " + columnName + ";" + twoEnter);
                }

                rs = statement.executeQuery(String.format(sql, table));

                while (rs.next()) {
                    columnName = rs.getString("COLUMN_NAME").toLowerCase();
                    columnClassName = rs.getString("DATA_TYPE");
                    columnClass = firstToUpperCase(columnName);

                    if ("varchar".equals(columnClassName) || "mediumtext".equals(columnClassName)) {
                        attributeType = "String";
                    } else if ("int".equals(columnClassName)) {
                        attributeType = "int";
                    } else if ("bigint".equals(columnClassName)) {
                        attributeType = "long";
                    } else if ("decimal".equals(columnClassName)) {
                        attributeType = "BigDecimal";
                    }

                    fileWriter.write(oneTabStr + "public void set" + columnClass + "(" + attributeType + " " + columnName + ") {" + oneEnter);
                    fileWriter.write(twoTabStr + "this." + columnName + " = " + columnName + ";" + oneEnter + "    }" + twoEnter);
                    fileWriter.write(oneTabStr + "public " + attributeType + " get" + columnClass + "() {" + oneEnter);
                    fileWriter.write(twoTabStr + "return this." + columnName + ";" + oneEnter + "    }" + twoEnter);
                }
                fileWriter.write("}");
                fileWriter.close();
            }
        }
    }

    public static void generateMapper() throws SQLException, IOException {
        List<String> tables = getTables();

        if (tables.size() > 0) {
            String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='" + DB_NAME + "'";
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

                fileWriter = new FileWriter(MAPPER_PATH + className + ".java");
                fileWriter.write("import net.ys.bean." + beanName + ";" + oneEnter);
                fileWriter.write("import org.springframework.jdbc.core.RowMapper;" + oneEnter);
                fileWriter.write("import java.sql.ResultSet;" + oneEnter);
                fileWriter.write("import java.sql.SQLException;" + twoEnter);

                fileWriter.write("public class " + className + " implements RowMapper<" + beanName + "> {" + oneEnter);
                fileWriter.write(oneTabStr + "@Override" + oneEnter);
                fileWriter.write(oneTabStr + "public " + beanName + " mapRow(ResultSet resultSet, int i) throws SQLException {" + oneEnter);
                fileWriter.write(twoTabStr + beanName + " " + classObject + " = new " + beanName + "();" + oneEnter);

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

    public static void select() throws SQLException, IOException {
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='%s'";
        String columnName;
        rs = statement.executeQuery(String.format(sql, TABLE_NAME, DB_NAME));
        StringBuffer sb = new StringBuffer("SELECT ");
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME").toLowerCase();
            sb.append("`" + columnName + "`, ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" FROM `" + TABLE_NAME + "` WHERE 1 = 1");
        System.out.println(sb.toString());
    }

    public static void update() throws SQLException, IOException {
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='%s'";
        String columnName;
        rs = statement.executeQuery(String.format(sql, TABLE_NAME, DB_NAME));
        StringBuffer sb = new StringBuffer("UPDATE `").append(TABLE_NAME).append("` SET ");
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

    public static void insert() throws SQLException, IOException {
        String sql = "SELECT COLUMN_NAME FROM information_schema.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA='%s'";
        String columnName;
        rs = statement.executeQuery(String.format(sql, TABLE_NAME, DB_NAME));
        StringBuffer sb = new StringBuffer("INSERT INTO `").append(TABLE_NAME).append("` (");
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
        sb.append(") VALUES (").append(genMark(size)).append(")");
        System.out.println(sb.toString());
    }

    public static String genMark(int size) {
        StringBuffer sb = new StringBuffer("?");
        for (int i = 1; i < size; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    public static List<String> getTables() throws SQLException {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA. TABLES WHERE TABLE_SCHEMA = '" + DB_NAME + "'";

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

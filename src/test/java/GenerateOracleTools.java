import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateOracleTools {

    public static void main(String[] args) throws Exception {
        generateBean();
        statement.close();
        connection.close();
    }

    static ResultSet rs = null;
    static String oneEnter = "\r\n";
    static String twoEnter = "\r\n\r\n";
    static String oneTabStr = "\t";

    static final String DB_NAME = "ORCL";
    static final String URL = "jdbc:oracle:thin:@10.30.30.24:1521/" + DB_NAME;
    static final String USER_NAME = "DEP_TEST";
    static final String PASSWORD = "DEP_TEST";
    static final String BEAN_PATH = "D:/bean/";
    static Connection connection = null;
    static Statement statement = null;

    static {
        try {
            File file = new File(BEAN_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateBean() throws SQLException, IOException {
        List<String> tables = getTables();

        if (tables.size() > 0) {
            String sql = "SELECT ATC.COLUMN_NAME, ATC.DATA_TYPE, UCC.COMMENTS AS COLUMN_COMMENT FROM all_tab_columns ATC, user_col_comments UCC WHERE UCC.TABLE_NAME = ATC.TABLE_NAME AND UCC.COLUMN_NAME = ATC.COLUMN_NAME AND ATC.TABLE_NAME = '%s'";
            String columnName;
            String columnClassName;
            String columnComment;
            String fileName;
            FileWriter fileWriter;
            String attributeType;
            for (String table : tables) {
                fileName = camelFormat(table, true);

                fileWriter = new FileWriter(BEAN_PATH + fileName + ".java");

                rs = statement.executeQuery("SELECT COMMENTS AS TABLE_COMMENT FROM user_tab_comments WHERE TABLE_NAME = '" + table + "'");
                String tableComment = "";
                while (rs.next()) {
                    tableComment = rs.getString("TABLE_COMMENT");
                    tableComment = tableComment == null ? "" : tableComment;
                    break;
                }

                fileWriter.write("import java.io.Serializable;" + oneEnter);
                fileWriter.write("/**" + oneEnter);
                fileWriter.write("* " + tableComment + oneEnter);
                fileWriter.write("*/" + oneEnter);
                fileWriter.write("public class " + fileName + " implements Serializable {" + twoEnter);

                rs = statement.executeQuery(String.format(sql, table));
                while (rs.next()) {
                    columnName = camelFormat(rs.getString("COLUMN_NAME").toLowerCase(), false);
                    columnClassName = rs.getString("DATA_TYPE").toLowerCase();
                    columnComment = rs.getString("COLUMN_COMMENT");
                    columnComment = columnComment == null ? "" : columnComment;

                    if ("number".equals(columnClassName)) {
                        attributeType = "int";
                    } else {
                        attributeType = "String";
                    }
                    fileWriter.write(oneTabStr + "private " + attributeType + " " + columnName + ";\t//" + columnComment + twoEnter);
                }

                fileWriter.write("}");
                fileWriter.close();
            }
        }
    }

    public static List<String> getTables() throws SQLException {
        String sql = "SELECT TABLE_NAME FROM user_tab_comments";

        rs = statement.executeQuery(sql);
        List<String> tables = new ArrayList<String>();
        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME").toLowerCase());
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
            String[] strings = resource.toLowerCase().split("_+");
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

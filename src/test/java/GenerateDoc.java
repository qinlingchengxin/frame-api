import net.sf.jxls.transformer.XLSTransformer;
import net.ys.util.LogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: NMY
 * Date: 18-12-25
 */
public class GenerateDoc {

    static final String DB_NAME = "sign";
    static final String URL = "jdbc:mysql://10.10.10.51:3306/" + DB_NAME;
    static final String USER_NAME = "root";
    static final String PASSWORD = "zhulong123321";
    static final String DOC_PATH = "D:/bean/";

    public static void main(String[] args) {
        genDoc();
    }

    public static void genDoc() {
        try {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, String>> fields = getAllFieldsMySql();
            if (fields.size() == 0) {
                return;
            }
            map.put("records", fields);
            String templateFileName = GenerateDoc.class.getResource("/doc.xls").getPath();
            String resultFileName = "doc-" + System.currentTimeMillis() + ".xls";
            FileOutputStream fos = new FileOutputStream(DOC_PATH + resultFileName);
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(templateFileName));
            XLSTransformer transformer = new XLSTransformer();
            Workbook wb = transformer.transformXLS(is, map);
            wb.write(fos);
            is.close();
            fos.close();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public static List<Map<String, String>> getAllFieldsMySql() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("SELECT c.TABLE_NAME, t.TABLE_COMMENT, c.COLUMN_NAME, c.DATA_TYPE, c.COLUMN_COMMENT FROM information_schema.`COLUMNS` c LEFT JOIN information_schema.`TABLES` t ON t.TABLE_SCHEMA = c.TABLE_SCHEMA AND t.TABLE_NAME = c.TABLE_NAME WHERE c.TABLE_SCHEMA = ? AND t.TABLE_TYPE = 'BASE TABLE' ORDER BY c.TABLE_NAME, c.COLUMN_NAME");
            statement.setString(1, DB_NAME);
            ResultSet rs = statement.executeQuery();
            List<Map<String, String>> fields = new ArrayList<>();
            String tableName;
            String tableComment;
            String columnName;
            String columnType;
            String columnComment;
            Map<String, String> map;
            while (rs.next()) {
                tableName = rs.getString("TABLE_NAME");
                tableComment = rs.getString("TABLE_COMMENT") == null ? "" : rs.getString("TABLE_COMMENT");
                columnName = rs.getString("COLUMN_NAME");
                columnType = rs.getString("DATA_TYPE");
                columnComment = rs.getString("COLUMN_COMMENT");
                if (StringUtils.isBlank(columnComment)) {
                    columnComment = columnName;
                }

                map = new HashMap<>();
                map.put("tableId", tableName + "\n" + tableComment);
                map.put("fieldName", columnName);
                map.put("fieldType", columnType);
                map.put("comment", columnComment);
                fields.add(map);
            }
            rs.close();
            statement.close();
            connection.close();
            return fields;
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return null;
    }
}

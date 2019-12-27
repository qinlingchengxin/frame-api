import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class InitTable {

    public static void main(String[] args) throws Exception {
        initMysqlTable();
        //initOracleTable();
    }

    private static void initMysqlTable() {
        try {
            Map<String, String> tables = new HashMap<>();
            for (int i = 0; i < 20; i++) {
                tables.put("animal_" + i, "宠物表" + i);
            }
            String url = "jdbc:mysql://10.40.40.139:3306/test_one";
            String userName = "root";
            String password = "root";

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, userName, password);
            Statement statement = connection.createStatement();

            String sqlTemp = "CREATE TABLE IF NOT EXISTS `%s` (" +
                    "`id` BIGINT (20) NOT NULL AUTO_INCREMENT COMMENT '主键'," +
                    "`create_time` BIGINT (20) NOT NULL DEFAULT 0 COMMENT '创建时间(13位Long类型时间戳)'," +
                    "`update_time` BIGINT (20) NOT NULL DEFAULT 0 COMMENT '修改时间(13位Long类型时间戳)'," +
                    "`is_deleted` INT (1) NOT NULL DEFAULT 1 COMMENT '是否删除：0-无效/1-有效'," +
                    "`sys_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP COMMENT '系统变更时间'," +
                    "PRIMARY KEY (`id`)" +
                    ") COMMENT = '%s';";

            for (Map.Entry<String, String> entry : tables.entrySet()) {
                String sql = String.format(sqlTemp, entry.getKey().toLowerCase(), entry.getValue());
                statement.execute(sql);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initOracleTable() {
        try {
            Map<String, String> tables = new HashMap<>();
            for (int i = 0; i < 2; i++) {
                tables.put("person" + i, "用户表" + i);
            }
            String url = "jdbc:oracle:thin:@10.30.30.24:1521/ORCL";
            String userName = "SIGNATURE";
            String password = "SIGNATURE";
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection connection = DriverManager.getConnection(url, userName, password);
            Statement statement = connection.createStatement();
            String tableName;
            for (Map.Entry<String, String> entry : tables.entrySet()) {
                tableName = entry.getKey().toUpperCase();
                statement.execute("CREATE TABLE \"" + tableName + "\" (\"ID\" VARCHAR2 (50) DEFAULT '' NOT NULL,\"CREATE_TIME\" NUMBER (20) DEFAULT 0 NOT NULL,\"UPDATE_TIME\" NUMBER (20) DEFAULT 0 NOT NULL,\"IS_DELETED\" NUMBER DEFAULT 1 NOT NULL,PRIMARY KEY (\"ID\"))");
                statement.execute("COMMENT ON TABLE \"" + tableName + "\" IS '" + entry.getValue() + "'");
                statement.execute("COMMENT ON COLUMN \"" + tableName + "\".\"ID\" IS '主键'");
                statement.execute("COMMENT ON COLUMN \"" + tableName + "\".\"CREATE_TIME\" IS '创建时间(13位Long类型时间戳)'");
                statement.execute("COMMENT ON COLUMN \"" + tableName + "\".\"UPDATE_TIME\" IS '修改时间(13位Long类型时间戳)'");
                statement.execute("COMMENT ON COLUMN \"" + tableName + "\".\"IS_DELETED\" IS '是否删除：0-未删除/1-已删除'");
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

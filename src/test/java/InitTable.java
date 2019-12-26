import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class InitTable {

    public static void main(String[] args) throws Exception {
        Map<String, String> tables = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            tables.put("animal" + i, "宠物表" + i);
        }

        for (Map.Entry<String, String> entry : tables.entrySet()) {
            initTable(entry.getKey(), entry.getValue());
        }
    }

    private static void initTable(String tableName, String comment) {
        try {
            String url = "jdbc:mysql://10.40.40.139:3306/test_one";
            String userName = "root";
            String password = "root";

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, userName, password);
            String sql = "CREATE TABLE `" + tableName + "` (\n" +
                    "\t`id` BIGINT (20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                    "\t`create_time` BIGINT (20) NOT NULL DEFAULT 0 COMMENT '创建时间',\n" +
                    "\t`update_time` BIGINT (20) NOT NULL DEFAULT 0 COMMENT '修改时间',\n" +
                    "\t`is_deleted` INT (1) NOT NULL DEFAULT 1 COMMENT '是否删除：0-无效/1-有效',\n" +
                    "\t`sys_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP COMMENT '系统变更时间',\n" +
                    "\tPRIMARY KEY (`id`)\n" +
                    ") COMMENT = '" + comment + "';\n";
            Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

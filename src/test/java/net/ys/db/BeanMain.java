package net.ys.db;

import java.io.IOException;
import java.sql.SQLException;

/**
 * User: NMY
 * Date: 17-6-12
 */
public class BeanMain {

    public static final String DB_NAME = "sign";
    public static final String TABLE_NAME = "sys_admin";
    public static final String URL = "jdbc:mysql://10.40.40.93:3306/" + DB_NAME;
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "root";
    public static final String BEAN_PATH = "D:/bean/";
    public static final String MAPPER_PATH = "D:/bean/mapper/";
    public static final String RESP_MAPPER_PATH = "D:/bean/ResponseMapper/";

    public static void main(String[] args) throws IOException, SQLException {
        GenerateBean.generateBean(DB_NAME);
        GenerateMapper.generateBean(DB_NAME);
        GenerateBeanRespMapper.generateBean(DB_NAME);
        GenerateSelectAll.generateFields(DB_NAME, TABLE_NAME);
    }
}

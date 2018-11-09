package net.ys.mapper;

import net.ys.bean.SysAdmin;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SysAdminMapper implements RowMapper<SysAdmin> {
    @Override
    public SysAdmin mapRow(ResultSet resultSet, int i) throws SQLException {
        SysAdmin admin = new SysAdmin();
        admin.setId(resultSet.getString("id"));
        admin.setMagType(resultSet.getInt("mag_type"));
        admin.setUsername(resultSet.getString("username"));
        admin.setPwd(resultSet.getString("pwd"));
        return admin;
    }
}
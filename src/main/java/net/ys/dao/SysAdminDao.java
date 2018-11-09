package net.ys.dao;

import net.ys.bean.SysAdmin;
import net.ys.mapper.SysAdminMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SysAdminDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public SysAdmin queryAdmin(String username, String pass) {
        String sql = "SELECT id, mag_type, username, pwd FROM sys_admin WHERE username = ? AND pwd =?";
        List<SysAdmin> list = jdbcTemplate.query(sql, new SysAdminMapper(), username, pass);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}

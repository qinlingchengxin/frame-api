package net.ys.dao;

import net.ys.bean.SysAdmin;
import net.ys.constant.S;
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
        List<SysAdmin> list = jdbcTemplate.query(S.ADMIN_SELECT, new SysAdminMapper(), username, pass);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}

package net.ys.dao;

import net.ys.bean.Person;
import net.ys.mapper.PersonMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class PersonDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public long queryPersonCount(String name) {

        if ("".equals(name)) {
            String sql = "SELECT COUNT(*) FROM person";
            return jdbcTemplate.queryForObject(sql, Long.class);
        }

        String sql = "SELECT COUNT(*) FROM person WHERE name LIKE ?";
        return jdbcTemplate.queryForObject(sql, Long.class, "%" + name + "%");
    }

    public List<Person> queryPersons(String name, int page, int pageSize) {

        if ("".equals(name)) {
            String sql = "SELECT id, name, age, create_time FROM person LIMIT ?,?";
            return jdbcTemplate.query(sql, new PersonMapper(), (page - 1) * pageSize, pageSize);
        }
        String sql = "SELECT id, name, age, create_time FROM person WHERE name LIKE ? LIMIT ?,?";
        return jdbcTemplate.query(sql, new PersonMapper(), name, (page - 1) * pageSize, pageSize);
    }
}

package net.ys.dao;

import net.ys.bean.Person;
import net.ys.constant.S;
import net.ys.mapper.PersonMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class PersonDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public long queryPersonCount(String name) {

        if ("".equals(name)) {
            return jdbcTemplate.queryForObject(S.PERSON_COUNT, Long.class);
        }
        return jdbcTemplate.queryForObject(S.PERSON_COUNT_LIKE, Long.class, "%" + name + "%");
    }

    public List<Person> queryPersons(String name, int page, int pageSize) {

        if ("".equals(name)) {
            return jdbcTemplate.query(S.PERSON_LIST, new PersonMapper(), (page - 1) * pageSize, pageSize);
        }
        return jdbcTemplate.query(S.PERSON_LIST_LIKE, new PersonMapper(), name, (page - 1) * pageSize, pageSize);
    }

    public Person queryPerson(String id) {
        List<Person> list = jdbcTemplate.query(S.PERSON_SELECT, new PersonMapper(), id);
        if (list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public boolean savePerson(Person person) {
        return jdbcTemplate.update(S.PERSON_UPDATE, person.getName(), person.getAge(), person.getId()) >= 0;
    }

    public Person addPerson(final Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(S.PERSON_INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, person.getName());
                ps.setInt(2, person.getAge());
                ps.setLong(3, System.currentTimeMillis());
                return ps;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();

        if (id > 0) {
            List<Person> persons = jdbcTemplate.query(S.PERSON_SELECT, new PersonMapper());
            if (persons.size() > 0) {
                return persons.get(0);
            }
        }

        return null;
    }
}

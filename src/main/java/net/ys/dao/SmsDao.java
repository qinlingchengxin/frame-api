package net.ys.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class SmsDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void addSmsRecord(String phoneNumber) {
        String sql = "INSERT INTO sms_log (phone_num, create_time) VALUES (?,?)";
        jdbcTemplate.update(sql, phoneNumber, System.currentTimeMillis());
    }
}

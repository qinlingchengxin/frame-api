package net.ys.service;

import net.ys.bean.Person;
import net.ys.dao.PersonDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PersonService {
    @Resource
    private PersonDao busDao;

    public long queryPersonCount(String name) {
        return busDao.queryPersonCount(name);
    }

    public List<Person> queryPersons(String name, int page, int pageSize) {
        return busDao.queryPersons(name, page, pageSize);
    }
}

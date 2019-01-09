package net.ys.controller;

import net.ys.bean.Person;
import net.ys.constant.GenResult;
import net.ys.service.PersonService;
import net.ys.utils.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "web")
@ApiIgnore
public class PersonController {

    @Resource
    private PersonService personService;

    @RequestMapping(value = "persons")
    public ModelAndView list(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "1") int page, @RequestParam(value = "page_size", defaultValue = "10") int pageSize) {
        ModelAndView modelAndView = new ModelAndView("person/list");

        if (page < 1) {
            page = 1;
        }

        long count = personService.queryPersonCount(name);

        long t = count / pageSize;
        int k = count % pageSize == 0 ? 0 : 1;
        int totalPage = (int) (t + k);

        if (page > totalPage && count > 0) {
            page = totalPage;
        }

        List<Person> persons;
        if ((page - 1) * pageSize < count) {
            persons = personService.queryPersons(name, page, pageSize);
        } else {
            persons = new ArrayList<Person>();
        }

        modelAndView.addObject("count", count);
        modelAndView.addObject("curr_page", page);
        modelAndView.addObject("total_page", totalPage);
        modelAndView.addObject("persons", persons);
        modelAndView.addObject("name", name);
        return modelAndView;
    }

    @RequestMapping(value = "person/edit")
    public ModelAndView edit(@RequestParam(defaultValue = "") String id) {
        ModelAndView modelAndView = new ModelAndView("person/edit");
        Person person;
        if ("".equals(id)) {//新增
            person = new Person();
        } else {
            person = personService.queryPerson(id);
        }

        modelAndView.addObject("person", person);
        return modelAndView;
    }

    @RequestMapping(value = "person/save")
    @ResponseBody
    public Map<String, Object> save(Person person) {

        boolean flag = personService.savePerson(person);

        if (!flag) {
            return GenResult.FAILED.genResult();
        }
        return GenResult.SUCCESS.genResult();
    }

    @RequestMapping(value = "person/add")
    @ResponseBody
    public Map<String, Object> add(Person person) {
        try {
            person = personService.addPerson(person);
            if (person == null) {
                return GenResult.FAILED.genResult();
            }
            return GenResult.SUCCESS.genResult(person);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }
}

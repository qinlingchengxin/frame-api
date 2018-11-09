package net.ys.controller;

import net.ys.bean.Person;
import net.ys.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "web/person")
@ApiIgnore
public class PersonController {

    @Resource
    private PersonService busService;

    @RequestMapping(value = "list")
    public ModelAndView list(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        ModelAndView modelAndView = new ModelAndView("person/list");

        if (page < 1) {
            page = 1;
        }

        long count = busService.queryPersonCount(name);

        long t = count / pageSize;
        int k = count % pageSize == 0 ? 0 : 1;
        int totalPage = (int) (t + k);

        if (page > totalPage && count > 0) {
            page = totalPage;
        }

        List<Person> persons;
        if ((page - 1) * pageSize < count) {
            persons = busService.queryPersons(name, page, pageSize);
        } else {
            persons = new ArrayList<Person>();
        }

        modelAndView.addObject("count", count);
        modelAndView.addObject("currPage", page);
        modelAndView.addObject("totalPage", totalPage);
        modelAndView.addObject("persons", persons);
        modelAndView.addObject("name", name);
        return modelAndView;
    }
}

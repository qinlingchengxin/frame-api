package net.ys.controller;

import net.ys.bean.SysAdmin;
import net.ys.constant.GenResult;
import net.ys.service.AdminService;
import net.ys.utils.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value = "web/admin")
@ApiIgnore
public class AdminController {

    @Resource
    private AdminService adminService;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String doGet() {
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String doPost(HttpSession session, String username, String password) {
        try {
            SysAdmin admin = adminService.queryAdmin(username, password);
            if (admin != null) {
                session.setAttribute("admin", admin);
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return "redirect:main";
    }

    @RequestMapping(value = "main")
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView("main");
        return modelAndView;
    }

    @RequestMapping(value = "left")
    public ModelAndView left() {
        ModelAndView modelAndView = new ModelAndView("left");
        return modelAndView;
    }

    @RequestMapping(value = "top")
    public ModelAndView top() {
        ModelAndView modelAndView = new ModelAndView("top");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String now = simpleDateFormat.format(new Date());
        modelAndView.addObject("now", now);
        return modelAndView;
    }

    @RequestMapping(value = "footer")
    public ModelAndView footer() {
        ModelAndView modelAndView = new ModelAndView("footer");
        return modelAndView;
    }

    @RequestMapping(value = "logout")
    @ResponseBody
    public Map<String, Object> logout(HttpSession session) {
        session.removeAttribute("admin");
        return GenResult.SUCCESS.genResult();
    }
}

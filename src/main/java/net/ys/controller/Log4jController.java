package net.ys.controller;

import net.ys.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequestMapping(value = "log4j")
public class Log4jController {

    private String logRoot;

    @PostConstruct
    public void init() {
        try {
            Properties properties = new Properties();
            properties.load(Log4jController.class.getClassLoader().getResourceAsStream("log4j.properties"));
            String tmp = properties.getProperty("root");
            String home = System.getProperty("catalina.home").replace('\\', '/');
            logRoot = tmp.replace("${catalina.home}", home);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "log", method = RequestMethod.GET)
    public ModelAndView log() {
        ModelAndView mv = new ModelAndView("log4j");
        try {
            List<Map<String, String>> files = new ArrayList<Map<String, String>>();
            File[] list = new File(logRoot).listFiles();
            for (File s : list) {
                String tmp = s.getAbsolutePath().replace('\\', '/');
                tmp = tmp.substring(tmp.indexOf(':') + 1);
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", tmp);
                map.put("url", URLEncoder.encode(tmp, "utf-8"));
                files.add(map);
            }
            mv.addObject("files", files);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        return mv;
    }

    @RequestMapping(value = "content", method = RequestMethod.GET)
    public void content(HttpServletResponse response, String path) throws IOException {
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/plain");
        ServletOutputStream out = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = inputStream.read(bytes)) > 0) {
            out.write(bytes, 0, len);
            out.flush();
        }
        out.close();
        inputStream.close();
    }
}

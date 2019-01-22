package net.ys.controller;

import net.ys.constant.GenResult;
import net.ys.job.JobService;
import net.ys.utils.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 定时任务测试接口
 */
@Controller
@RequestMapping(value = "job", produces = {"application/json;charset=utf-8"})
@ApiIgnore
public class JobController {

    @Resource
    private JobService jobService;

    @RequestMapping(value = "call/{method}", headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> call(@PathVariable("method") String method) {
        long start = System.currentTimeMillis();
        try {
            Method m = JobService.class.getMethod(method);
            m.invoke(jobService);
            return GenResult.SUCCESS.genResult(System.currentTimeMillis() - start);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult(System.currentTimeMillis() - start);
        }
    }
}

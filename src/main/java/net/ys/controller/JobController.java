package net.ys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.ys.constant.GenResult;
import net.ys.job.JobService;
import net.ys.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 定时任务测试接口
 */
@Controller
@RequestMapping(value = "job", produces = {"application/json;charset=utf-8"})
@Api(value = "job-api", description = "定时任务")
public class JobController {

    @Resource
    private JobService jobService;

    @RequestMapping(value = "call/{method}", headers = "Accept=application/json")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "总接口")
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

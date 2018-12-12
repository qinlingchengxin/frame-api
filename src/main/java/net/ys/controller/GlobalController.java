package net.ys.controller;

import net.ys.constant.GenResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 处理全局请求
 */
@RestController
@RequestMapping(value = "global")
@ApiIgnore
public class GlobalController {

    @RequestMapping(value = "http_400", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> http400() {
        return GenResult.PARAMS_ERROR.genResult();
    }

    @RequestMapping(value = "http_405", headers = "Accept=application/json")
    public Map<String, Object> http405() {
        return GenResult.REQUEST_METHOD_ERROR.genResult();
    }
}

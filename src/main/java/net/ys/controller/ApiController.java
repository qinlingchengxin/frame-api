package net.ys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.ys.constant.GenResult;
import net.ys.utils.LogUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "api", produces = {"application/json;charset=utf-8"})
@Api(value = "frame-api", description = "接口")
public class ApiController {

    @RequestMapping(value = "test", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "测试")
    public Map<String, Object> test() {
        try {
            return GenResult.SUCCESS.genResult();
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }
}

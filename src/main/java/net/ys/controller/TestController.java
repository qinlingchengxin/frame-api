package net.ys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.ys.component.SysConfig;
import net.ys.constant.GenResult;
import net.ys.util.LogUtil;
import net.ys.util.PropertyUtil;
import net.ys.util.Tools;
import net.ys.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 测试接口
 */
@Controller
@RequestMapping(value = "test", produces = {"application/json;charset=utf-8"})
@Api(value = "test job", description = "测试接口")
public class TestController {

    @ResponseBody
    @RequestMapping(value = "reload/config", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "重新加载配置")
    public Map<String, Object> reloadConfig(@ApiParam(required = true, value = "配置", defaultValue = "") @RequestParam(required = true, defaultValue = "") String config) {
        try {
            String val = PropertyUtil.reGet(config);
            String fieldName = Tools.camelFormat(config);
            Field field = SysConfig.class.getField(fieldName);
            Class<?> type = field.getType();
            if (type == int.class) {
                field.set(null, Integer.parseInt(val));
            } else if (type == long.class) {
                field.set(null, Long.parseLong(val));
            } else {
                field.set(null, val);
            }

            SysConfig.testName = PropertyUtil.reGets("test_name.");//list类型放在最后，直接内置
            return GenResult.SUCCESS.genResult(config + "---->" + field.get(null));
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }

    @ResponseBody
    @RequestMapping(value = "client/ip", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "获取客户端ip")
    public Map<String, Object> clientIp(HttpServletRequest request) {
        try {
            String clientIP = WebUtil.getClientIP(request);
            return GenResult.SUCCESS.genResult(clientIP);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }
}


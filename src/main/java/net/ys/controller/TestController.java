package net.ys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.ys.component.SysConfig;
import net.ys.constant.GenResult;
import net.ys.util.*;
import net.ys.util.req.HttpClient;
import net.ys.util.req.HttpResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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

    /**
     * 升级程序,只需要将项目内部的文件按照项目结构目录打包成zip文件即可
     *
     * @param file zip文件，升级包
     * @return
     */
    @RequestMapping(value = "upgrade", headers = "Accept=application/json")
    @ResponseBody
    public Map<String, Object> upgrade(HttpServletRequest request, @RequestParam(required = true) MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upgrade_", ".zip");
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            String desPath = request.getServletContext().getRealPath("/").replaceAll("\\\\", "/");
            ZipFileUtil.readZipFile(tempFile.getAbsolutePath(), desPath);
            tempFile.delete();
            TomcatUtil.stopTomcat();
            return GenResult.SUCCESS.genResult();
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }

    @ResponseBody
    @RequestMapping(value = "proxy", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "测试代理")
    public Map<String, Object> proxy(HttpServletRequest request) {
        try {
            HttpResponse httpResponse = HttpClient.doGet("https://www.baidu.com");
            return GenResult.SUCCESS.genResult(httpResponse);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }

    @ResponseBody
    @RequestMapping(value = "app/name", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = Map.class, responseContainer = "Map", value = "应用名")
    public Map<String, Object> appName() {
        try {
            return GenResult.SUCCESS.genResult(SysConfig.appName);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }
}


package net.ys.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.ys.bean.Person;
import net.ys.constant.GenResult;
import net.ys.mapper.resp.PersonResp;
import net.ys.service.PersonService;
import net.ys.util.LogUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api", produces = {"application/json;charset=utf-8"})
@Api(value = "test-api", description = "测试接口")
public class ApiController {

    @Resource
    private PersonService personService;

    @RequestMapping(value = "persons", method = RequestMethod.GET, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "GET", response = PersonResp.class, responseContainer = "PersonResp", value = "用户列表")
    public Map<String, Object> persons(@ApiParam(required = true, value = "页码", defaultValue = "1") @RequestParam(required = true, value = "page", defaultValue = "1") int page,
                                       @ApiParam(required = true, value = "每页数量", defaultValue = "15") @RequestParam(required = true, value = "page_size", defaultValue = "15") int pageSize) {
        try {
            List<Person> persons = personService.queryPersons("", page, pageSize);
            return GenResult.SUCCESS.genResult(persons);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "POST", response = Map.class, responseContainer = "Map", value = "上传文件")
    public Map<String, Object> upload(@RequestParam(required = true) MultipartFile file) {
        try {
            long start = System.currentTimeMillis();
            File f = File.createTempFile("file-", ".zip");
            file.transferTo(f);
            System.out.println(f.getAbsoluteFile());
            Map<String, Object> result = new HashMap<>();
            result.put("name", f.getAbsolutePath());
            result.put("time", System.currentTimeMillis() - start);
            result.put("file_len", f.length());
            return GenResult.SUCCESS.genResult(result);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }

    @RequestMapping(value = "excel/import", method = RequestMethod.POST, headers = "Accept=application/json")
    @ApiOperation(httpMethod = "POST", response = Map.class, responseContainer = "Map", value = "上传excel")
    public Map<String, Object> importPerson(@RequestParam(required = true) MultipartFile file) {
        try {
            List<Person> persons = personService.readExcelPersons(file);
            return GenResult.SUCCESS.genResult(persons);
        } catch (Exception e) {
            LogUtil.error(e);
            return GenResult.UNKNOWN_ERROR.genResult();
        }
    }
}

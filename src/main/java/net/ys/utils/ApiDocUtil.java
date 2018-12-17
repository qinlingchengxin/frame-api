package net.ys.utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.ys.collection.StringMap;
import net.ys.constant.GenResult;
import net.ys.controller.GlobalController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1、导出ApiDoc文档，所有注解请遵循测试api
 * 2、导出的excel文件后进行编辑，合并通用模块
 * 3、response = PersonResp.class,response 必须自定义返回实体
 * User: NMY
 * Date: 18-12-15
 */
public class ApiDocUtil {

    /**
     * 获取数据
     *
     * @return
     */
    public static List<Map<String, String>> genRecords() throws Exception {
        List<Class> classes = ClassUtil.getClasses(GlobalController.class.getPackage().getName());
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record;
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(Api.class)) {

                RequestMapping classAnnotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                String prefix = "/" + classAnnotation.value()[0];
                Method[] methods = clazz.getMethods();

                for (Method method : methods) {
                    if (method.isAnnotationPresent(ApiOperation.class)) {

                        ApiOperation apiAnnotation = method.getAnnotation(ApiOperation.class);
                        RequestMapping requestAnnotation = method.getAnnotation(RequestMapping.class);

                        String name = apiAnnotation.value();
                        String url = prefix + "/" + requestAnnotation.value()[0];
                        String requestMethod = apiAnnotation.httpMethod();
                        Object o = apiAnnotation.response().newInstance();
                        String resExample = GenResult.SUCCESS.toJson(o);
                        Parameter[] parameters = method.getParameters();
                        if (parameters.length > 0) {
                            for (Parameter parameter : parameters) {
                                if (parameter.isAnnotationPresent(ApiParam.class)) {
                                    record = new StringMap<>();
                                    ApiParam paramAnnotation = parameter.getAnnotation(ApiParam.class);
                                    RequestParam reqAnnotation = parameter.getAnnotation(RequestParam.class);
                                    String param = reqAnnotation.value();
                                    String paramName = paramAnnotation.value();
                                    String paramType = parameter.getType().getName();
                                    record.put("name", name);
                                    record.put("url", url);
                                    record.put("method", requestMethod);
                                    record.put("resExample", resExample);
                                    record.put("parameter", param);
                                    record.put("parameterDesc", paramName);
                                    record.put("parameterType", paramType);
                                    records.add(record);
                                }
                            }
                        } else {
                            record = new StringMap<>();
                            record.put("name", name);
                            record.put("url", url);
                            record.put("method", requestMethod);
                            record.put("resExample", resExample);
                            record.put("parameter", "");
                            record.put("parameterDesc", "");
                            record.put("parameterType", "");
                            records.add(record);
                        }
                    }
                }
            }
        }
        return records;
    }
}

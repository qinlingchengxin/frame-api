package net.ys.utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出ApiDoc文档，所有注解请遵循测试api，导出的excel文件后进行编辑，合并通用模块
 * User: NMY
 * Date: 18-12-15
 */
public class ApiDoc {

    public static void main(String[] args) throws Exception {
        String path = "D:";//存储路径
        export(path);
    }

    /**
     * 导出excel
     *
     * @param path
     * @throws Exception
     */
    public static void export(String path) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, String>> records = genRecords();
        map.put("records", records);
        String templateFileName = ApiDoc.class.getClassLoader().getResource("template/api.xls").getPath();
        String resultFileName = "api_docs-" + System.currentTimeMillis() + ".xls";
        FileOutputStream fos = new FileOutputStream(path + "/" + resultFileName);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(templateFileName));
        XLSTransformer transformer = new XLSTransformer();
        Workbook wb = transformer.transformXLS(is, map);
        wb.write(fos);
        is.close();
        fos.close();
    }

    /**
     * 获取数据
     *
     * @return
     */
    public static List<Map<String, String>> genRecords() {
        List<Class> classes = ClassUtil.getClasses("net.ys.controller");
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

                        Parameter[] parameters = method.getParameters();
                        if (parameters.length > 0) {
                            for (Parameter parameter : parameters) {
                                if (parameter.isAnnotationPresent(ApiParam.class)) {
                                    record = new HashMap<>();
                                    ApiParam paramAnnotation = parameter.getAnnotation(ApiParam.class);
                                    String param = paramAnnotation.name();
                                    String paramName = paramAnnotation.value();
                                    String paramType = parameter.getType().getName();
                                    record.put("name", name);
                                    record.put("url", url);
                                    record.put("method", requestMethod);
                                    record.put("parameter", param);
                                    record.put("parameterDesc", paramName);
                                    record.put("parameterType", paramType);
                                    records.add(record);
                                }
                            }
                        } else {
                            record = new HashMap<>();
                            record.put("name", name);
                            record.put("url", url);
                            record.put("method", requestMethod);
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

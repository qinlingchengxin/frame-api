package net.ys.controller;

import net.sf.jxls.transformer.XLSTransformer;
import net.ys.constant.GenResult;
import net.ys.utils.ApiDocUtil;
import net.ys.utils.LogUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理全局请求
 */
@RestController
@RequestMapping(value = "global")
@ApiIgnore
public class GlobalController {

    @RequestMapping(value = "code/400", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> http400() {
        return GenResult.PARAMS_ERROR.genResult();
    }

    @RequestMapping(value = "code/405", headers = "Accept=application/json")
    public Map<String, Object> http405() {
        return GenResult.REQUEST_METHOD_ERROR.genResult();
    }

    @RequestMapping(value = "result/code", method = RequestMethod.GET)
    public ModelAndView resultCode() {
        ModelAndView mv = new ModelAndView("result_code");
        GenResult[] results = GenResult.values();
        mv.addObject("results", results);
        return mv;
    }

    @RequestMapping("api/export")
    public void exportFace(HttpServletRequest request, HttpServletResponse response) {

        ServletOutputStream os = null;
        InputStream is = null;
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            List<Map<String, String>> records = ApiDocUtil.genRecords();
            GenResult[] results = GenResult.values();
            map.put("records", records);
            map.put("results", results);
            String templateFileName = request.getSession().getServletContext().getRealPath("/template/api.xls");
            String resultFileName = "api_docs_" + System.currentTimeMillis() + ".xls";
            os = response.getOutputStream();
            is = new BufferedInputStream(new FileInputStream(templateFileName));
            XLSTransformer transformer = new XLSTransformer();
            Workbook wb = transformer.transformXLS(is, map);
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + resultFileName);
            response.setContentType("application/msexcel");
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                LogUtil.error(e);
            }
        }
    }
}

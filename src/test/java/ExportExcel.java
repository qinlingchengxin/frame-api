import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 测试同时遍历行和列
 * User: NMY
 * Date: 19-4-12
 */
public class ExportExcel {

    public static final String EXPORT_PATH = "E:/";

    public static void main(String[] args) {
        export();
    }

    public static void export() {
        try {
            Map<String, Object> map = new HashMap<>();
            List<String> headers = Arrays.asList("序号", "姓名", "年龄");
            map.put("headers", headers);

            List<List<String>> records = new ArrayList<>();//此处是行列均为list类型
            List<String> data1 = Arrays.asList("no-11", "name-11", "11");
            List<String> data2 = Arrays.asList("no-22", "name-22", "22");
            List<String> data3 = Arrays.asList("no-33", "name-33", "33");
            List<String> data4 = Arrays.asList("no-44", "name-44", "44");
            records.add(data1);
            records.add(data2);
            records.add(data3);
            records.add(data4);

            map.put("records", records);
            String templateFileName = ExportExcel.class.getResource("/demo.xls").getPath();
            String resultFileName = "demo-" + System.currentTimeMillis() + ".xls";
            FileOutputStream fos = new FileOutputStream(EXPORT_PATH + resultFileName);
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(templateFileName));
            XLSTransformer transformer = new XLSTransformer();
            Workbook wb = transformer.transformXLS(is, map);
            wb.write(fos);
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

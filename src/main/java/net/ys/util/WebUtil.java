package net.ys.util;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制器工具类
 * User: NMY
 * Date: 16-9-8
 */
public class WebUtil {

    static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";

    static final String ENCODING = "UTF-8";

    static final String HG = "--";

    static final String HH = "\r\n";

    static final String LH = "\r\n\r\n";

    public static void request(String link, Map<String, String> params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", ENCODING);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            StringBuffer contentBody = new StringBuffer(HG).append(BOUNDARY);

            for (Map.Entry<String, String> param : params.entrySet()) {
                contentBody.append(HH).append("Content-Disposition: form-data; name=\"").append(param.getKey()).append("\"").append(LH).append(param.getValue()).append(HH).append(HG).append(BOUNDARY);
            }

            contentBody.append(BOUNDARY).append(HG).append(LH).append(HG).append(BOUNDARY).append(HG).append(HH);
            OutputStream out = connection.getOutputStream();
            out.write(contentBody.toString().getBytes(ENCODING));
            out.flush();
            out.close();
            InputStream in = connection.getInputStream();
            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                String resp = new String(bytes, 0, bytes.length);
                System.out.println(resp);
            }
        } catch (Exception e) {
            System.out.println("request failed---->" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 获取请求ip地址
     *
     * @param request
     * @param transLocalIp whether to transfer 127.0.0.1 to real ip
     * @return
     */
    public static String getClientIP(HttpServletRequest request, boolean transLocalIp) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("127.0.0.1".equals(ip) && transLocalIp) {
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getLocalHost();
            } catch (Exception e) {
            }
            ip = inetAddress.getHostAddress();
        }
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    public static void main(String[] args) {
        String link = "http://localhost:8080/etl/api/upload.do?table_name=person";
        Map<String, String> param = new HashMap<String, String>();
        param.put("data", "[{\"id\":1,\"name\":\"a\",\"age\":1},{\"id\":2,\"name\":\"b\",\"age\":2},{\"id\":3,\"name\":\"c\",\"age\":3}]");
        param.put("content", "world");
        request(link, param);
    }
}

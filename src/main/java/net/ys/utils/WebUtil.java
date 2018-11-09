package net.ys.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制器工具类
 * User: LiWenC
 * Date: 16-9-8
 */
public class WebUtil {

    static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";
    static final String ENCODING = "UTF-8";
    static final String HH = "\r\n";
    static final String HHM = "\r\n\r\n";
    static final String HG = "--";

    public static Map<String, String> request(Map<String, String> params, String url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", ENCODING);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuffer contentBody = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            contentBody.append(HG + BOUNDARY).append(HH).append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(HHM).append(entry.getValue()).append(HH);
        }

        contentBody.append(HG).append(BOUNDARY).append(HG).append(HH);

        String content = contentBody.toString();
        OutputStream out = connection.getOutputStream();
        out.write(content.getBytes(ENCODING));
        out.flush();
        out.close();

        InputStream in = connection.getInputStream();
        int statusCode = connection.getResponseCode();

        InputStreamReader reader = new InputStreamReader(in, ENCODING);
        BufferedReader buffer = new BufferedReader(reader);
        String inputLine;
        StringBuffer sb = new StringBuffer();
        while ((inputLine = buffer.readLine()) != null) {
            try {
                sb.append(inputLine);
            } catch (Exception e) {
            }
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put("code", String.valueOf(statusCode));
        result.put("msg", sb.toString());

        return result;
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
}

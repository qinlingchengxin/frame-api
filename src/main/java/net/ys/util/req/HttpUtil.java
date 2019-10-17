package net.ys.util.req;

import net.ys.component.SysConfig;
import net.ys.util.LogUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class HttpUtil {

    private static final String BOUNDARY = "----------HV2ymHFg03ehbqgZCaKO6jyH";
    private static final String ENCODING = "UTF-8";
    private static final String HH = "\r\n";
    private static final String HHM = "\r\n\r\n";
    private static final String HG = "--";
    private static final int TIMEOUT = 20000;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE_APP_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_XML = "text/xml";
    private static final String CONTENT_TYPE_APP_XML = "application/xml";
    private static final String CONTENT_TYPE_FORM_DATA = "multipart/form-data; boundary=" + BOUNDARY;

    public static HttpResponse doGet(String address) {

        HttpURLConnection connection = null;
        try {
            connection = genConnection(address, METHOD_GET, CONTENT_TYPE_URL_ENCODED);
            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, null);
        }
    }

    public static HttpResponse doPost(String address, Map<String, Object> params) {

        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            connection = genConnection(address, METHOD_POST, CONTENT_TYPE_URL_ENCODED);

            String parameter = genParameter(params);
            byte[] data = parameter.getBytes(ENCODING);
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            out = connection.getOutputStream();
            out.write(data);
            out.flush();

            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, out);
        }
    }

    public static HttpResponse doPostTextXml(String address, String xml) {
        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            connection = genConnection(address, METHOD_POST, CONTENT_TYPE_TEXT_XML);

            byte[] data = xml.getBytes(ENCODING);
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            out = connection.getOutputStream();
            out.write(data);
            out.flush();

            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, out);
        }
    }

    public static HttpResponse doPostAppXml(String address, String xml) {
        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            connection = genConnection(address, METHOD_POST, CONTENT_TYPE_APP_XML);

            byte[] data = xml.getBytes(ENCODING);
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            out = connection.getOutputStream();
            out.write(data);
            out.flush();

            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, out);
        }
    }

    public static HttpResponse doPostJson(String address, String json) {
        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            connection = genConnection(address, METHOD_POST, CONTENT_TYPE_APP_JSON);

            byte[] data = json.getBytes(ENCODING);
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));

            out = connection.getOutputStream();
            out.write(data);
            out.flush();

            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, out);
        }
    }

    public static HttpResponse doPostFormData(String address, Map<String, String> texts, Map<String, File> files) {
        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            connection = genConnection(address, METHOD_POST, CONTENT_TYPE_FORM_DATA);
            out = connection.getOutputStream();
            StringBuffer contentBody = new StringBuffer();

            if (texts != null && !texts.isEmpty()) {
                for (String key : texts.keySet()) {//text
                    contentBody.append(HG + BOUNDARY).append(HH).append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(HHM).append(texts.get(key)).append(HH);
                }
                out.write(contentBody.toString().getBytes(ENCODING));
            }

            if (files != null && !files.isEmpty()) {
                for (String key : files.keySet()) {//file
                    File file = files.get(key);
                    contentBody = new StringBuffer();
                    InputStream inputStream = new FileInputStream(file);
                    contentBody.append(HG + BOUNDARY).append(HH).append("Content-Disposition: form-data; name=\"").append(key).append("\"").append("; filename=\"").append(file.getName()).append("\"").append(HH);
                    contentBody.append("Content-Type:").append(getContentType(file.getAbsolutePath())).append("\"").append(HHM);
                    out.write(contentBody.toString().getBytes(ENCODING));

                    int len;
                    byte[] bytes = new byte[1024];
                    while ((len = inputStream.read(bytes)) > 0) {
                        out.write(bytes, 0, len);
                    }
                    out.write(HH.getBytes(ENCODING));
                    out.flush();
                    inputStream.close();
                }
            }

            contentBody = new StringBuffer();
            contentBody.append(HG).append(BOUNDARY).append(HG).append(HH);
            out.write(contentBody.toString().getBytes(ENCODING));

            out.flush();

            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            return new HttpResponse(responseCode, result);
        } catch (Exception e) {
            return new HttpResponse(9999, e.getMessage());
        } finally {
            close(connection, out);
        }
    }

    public static HttpURLConnection genConnection(String address, String method, String contentType) throws IOException {
        URL url = new URL(address);

        HttpURLConnection connection;
        if (SysConfig.enableProxy == 1) {
            System.out.println("enableProxy ---------------");
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(SysConfig.proxyHost, SysConfig.proxyPort));
            connection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }

        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("ENCODING", ENCODING);
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", contentType);
        return connection;
    }

    public static String genResult(InputStream inputStream) throws IOException {
        StringBuffer result = new StringBuffer();
        int len;
        byte[] bytes = new byte[1024];
        while ((len = inputStream.read(bytes)) > 0) {
            result.append(new String(bytes, 0, len, ENCODING));
        }
        inputStream.close();
        return result.toString();
    }

    public static String genParameter(Map<String, Object> params) {
        String parameter = "";
        if (params != null) {
            StringBuffer parameterBuffer = new StringBuffer("");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                parameterBuffer.append(entry.getKey()).append("=").append(entry.getValue() == null ? "" : String.valueOf(entry.getValue())).append("&");
            }
            parameter = parameterBuffer.deleteCharAt(parameterBuffer.length() - 1).toString();
        }
        return parameter;
    }

    public static void close(HttpURLConnection connection, OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        } catch (Exception e) {
        }
    }

    public static String getContentType(String filePath) {
        String type = null;
        try {
            type = Files.probeContentType(Paths.get(filePath));
        } catch (IOException e) {
        }

        if (type == null) {//无法获取的均按照流文件处理
            type = "application/octet-stream";
        }
        return type;
    }
}
package net.ys.util.req;

import net.ys.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        HttpResponse response = new HttpResponse();
        try {
            connection = genConnection(address, METHOD_GET, CONTENT_TYPE_URL_ENCODED);
            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            response.setCode(connection.getResponseCode());
            response.setValue(result);
        } catch (Exception e) {
            response = HttpResponse.error(e.getMessage());
        } finally {
            close(connection, null);
        }
        return response;
    }

    public static HttpResponse doPost(String address, Map<String, Object> params) {

        HttpResponse response = new HttpResponse();
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
            response.setCode(connection.getResponseCode());
            response.setValue(result);

        } catch (Exception e) {
            response = HttpResponse.error(e.getMessage());
        } finally {
            close(connection, out);
        }

        return response;
    }

    public static HttpResponse doPostTextXml(String address, String xml) {
        HttpResponse response = new HttpResponse();
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
            response.setCode(connection.getResponseCode());
            response.setValue(result);

        } catch (Exception e) {
            response = HttpResponse.error(e.getMessage());
        } finally {
            close(connection, out);
        }
        return response;
    }

    public static HttpResponse doPostAppXml(String address, String xml) {
        HttpResponse response = new HttpResponse();
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
            response.setCode(connection.getResponseCode());
            response.setValue(result);

        } catch (Exception e) {
            response = HttpResponse.error(e.getMessage());
        } finally {
            close(connection, out);
        }
        return response;
    }

    public static HttpResponse doPostJson(String address, String json) {
        HttpResponse response = new HttpResponse();
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
            response.setCode(connection.getResponseCode());
            response.setValue(result);

        } catch (Exception e) {
            response = HttpResponse.error(e.getMessage());
        } finally {
            close(connection, out);
        }
        return response;
    }

    public static HttpResponse doPostFormData(String address, Map<String, String> params) {
        HttpResponse response = new HttpResponse();
        HttpURLConnection connection = null;
        OutputStream out = null;
        try {
            connection = genConnection(address, METHOD_POST, CONTENT_TYPE_FORM_DATA);

            StringBuffer contentBody = new StringBuffer();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                contentBody.append(HG + BOUNDARY).append(HH).append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(HHM).append(entry.getValue()).append(HH);
            }
            contentBody.append(HG).append(BOUNDARY).append(HG).append(HH);

            String content = contentBody.toString();
            out = connection.getOutputStream();
            out.write(content.getBytes(ENCODING));
            out.flush();

            int responseCode = connection.getResponseCode();
            String result = genResult(connection.getInputStream());
            LogUtil.info("responseCode:" + responseCode + "\tresult:" + result);
            response.setCode(connection.getResponseCode());
            response.setValue(result);

        } catch (Exception e) {
            response = HttpResponse.error(e.getMessage());
        } finally {
            close(connection, out);
        }
        return response;
    }

    public static HttpURLConnection genConnection(String address, String method, String contentType) throws IOException {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
}
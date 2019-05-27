package net.ys.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    /**
     * 用户名
     */
    private static final String USERNAME_PARAM = "username";

    /**
     * 参数
     */
    private static final String PASSWORD_PARAM = "password";

    /**
     * 默认HTTP头信息
     */
    private static Map<String, String> defaultHeaders = new HashMap<String, String>();

    /**
     * 初始化默认HTTP头信息
     */
    static {
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.2)");
        defaultHeaders.put("Content-Type", "application/x-www-form-urlencoded");
    }

    /**
     * 登录后提交
     *
     * @param url
     * @param username
     * @param password
     * @param postUrl
     * @param params
     * @return
     * @throws Exception
     */
    public static String loginAndPost(String url, String username, String password, String postUrl, Map<String, Object> params) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        Map<String, Object> loginParams = new HashMap<String, Object>();
        loginParams.put(USERNAME_PARAM, username);
        loginParams.put(PASSWORD_PARAM, password);
        post(url, loginParams, httpClient);
        CookieStore cookieStore = httpClient.getCookieStore();
        httpClient = new DefaultHttpClient();
        httpClient.setCookieStore(cookieStore);
        return post(postUrl, params, httpClient);
    }

    public static String post(String url) throws Exception {
        return post(url, null, null);
    }

    public static String post(String url, DefaultHttpClient httpClient) throws Exception {
        return post(url, null, httpClient);
    }

    public static String post(String url, Map<String, Object> params) throws Exception {
        return post(url, params, null);
    }

    public static String post(String url, Map<String, Object> params, DefaultHttpClient httpClient) throws Exception {
        return post(url, defaultHeaders, params, httpClient);
    }

    public static String post(String url, Map<String, String> headers, Map<String, Object> params, DefaultHttpClient httpClient) throws Exception {
        logger.info("httpPost URL [" + url + "] start ");
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }

        //检查url是否为https请求，如果是的话增加免证书校验
        if (StringUtils.isNotBlank(url) && url.trim().length() > 5) {
            String httpHead = url.substring(0, 5);
            if ("https".equals(httpHead.toLowerCase())) {
                //Secure Protocol implementation.
                SSLContext ctx = SSLContext.getInstance("SSL");
                //Implementation of a trust manager for X509 certificates
                X509TrustManager tm = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                ctx.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory ssf = new SSLSocketFactory(ctx);

                ClientConnectionManager ccm = httpClient.getConnectionManager();
                SchemeRegistry sr = ccm.getSchemeRegistry();
                sr.register(new Scheme("https", 443, ssf));
            }
        }

        HttpPost httpPost = new HttpPost(url);
        try {
            if (headers != null) {
                setHeaders(httpPost, headers);
            } else {
                setHeaders(httpPost, defaultHeaders);
            }
            List<NameValuePair> nvps = setParams(params);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            setTimeouts(httpClient, httpPost);
            return readResponseBody(httpClient, httpPost);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    private static void setHeaders(HttpPost httpPost, Map<String, String> headers) {

        for (Entry<String, String> entry : headers.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
    }

    private static List<NameValuePair> setParams(Map<String, Object> params) {
        StringBuilder paramsLogBuilder = new StringBuilder();
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params == null) {
            return nvps;
        }
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                nvps.add(new BasicNameValuePair(key, ""));
                paramsLogBuilder.append("[" + key + "-]");
                continue;
            }
            if (String[].class.isInstance(value)) {
                String[] strArray = (String[]) value;
                for (String str : strArray) {
                    nvps.add(new BasicNameValuePair(key, str));
                }
                paramsLogBuilder.append("[" + key + "- " + Arrays.asList(strArray) + "]");
                continue;
            }
            nvps.add(new BasicNameValuePair(key, value.toString()));
        }
        logger.info("params:" + paramsLogBuilder.toString());
        return nvps;
    }

    private static void setTimeouts(DefaultHttpClient httpclient, HttpPost httpPost) {
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpPost.getParams(), 30000);
    }

    private static String readResponseBody(DefaultHttpClient httpclient, HttpPost httpPost) throws Exception {
        HttpResponse response = httpclient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            byte[] bytes = EntityUtils.toByteArray(entity);
            return new String(bytes, "UTF-8");
        }
        return "";
    }

    private static PoolingClientConnectionManager cm = null;

    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        //			schemeRegistry.register(
        //			         new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        cm = new PoolingClientConnectionManager(schemeRegistry);
        // 设置最大连接数
        cm.setMaxTotal(500);
        // 设置每个路由默认最大连接数
        cm.setDefaultMaxPerRoute(50);
    }

    public static DefaultHttpClient getHttpClient() {

        // HttpParams
        HttpParams httpParams = new BasicHttpParams();
        // HttpConnectionParams 设置连接参数
        // 设置连接超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        // 设置读取超时时间
        HttpConnectionParams.setSoTimeout(httpParams, 60000);
        // 设置代理和代理最大路由
        //			HttpHost localhost = new HttpHost("locahost", 80);
        //			cm.setMaxPerRoute(new HttpRoute(localhost), 50);
        // 设置代理，
        //	        HttpHost proxy = new HttpHost("10.36.24.3", 60001);
        //	        httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY,  proxy);

        return new DefaultHttpClient(cm, httpParams);
    }

    public static String postTest(String postUrl, Map<String, Object> params) throws Exception {
        return postTest(postUrl, params, getHttpClient());
    }

    public static String postTest(String url, Map<String, Object> params, DefaultHttpClient httpClient) throws Exception {
        return postTest(url, defaultHeaders, params, httpClient);
    }

    public static String postTest(String url, Map<String, String> headers, Map<String, Object> params, DefaultHttpClient httpClient) throws Exception {
        logger.info("httpPost URL [" + url + "] start ");
        if (httpClient == null) {
            httpClient = getHttpClient();
        }
        HttpPost httpPost = new HttpPost(url);
        try {
            if (headers != null) {
                setHeaders(httpPost, headers);
            } else {
                setHeaders(httpPost, defaultHeaders);
            }
            List<NameValuePair> nvps = setParams(params);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            //setTimeouts(httpClient, httpPost);
            return readResponseBody(httpClient, httpPost);
        } finally {
            //httpClient.getConnectionManager().shutdown();
        }
    }
}

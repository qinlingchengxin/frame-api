package net.ys.service;

import net.ys.constant.MsgKey;
import net.ys.threadpool.ThreadPoolManager;
import net.ys.utils.LogUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

@Service
public class DemoService {

    @Resource
    private MsgProducer msgProducer;

    public void testThread() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.debug("DemoService");
                } catch (Exception e) {
                    LogUtil.error(e);
                }
            }
        };
        ThreadPoolManager.INSTANCE.complexPool.doIt(r);
    }

    public void testPostMethod() {
        HttpClient httpclient = new HttpClient();
        PostMethod postMethod = new PostMethod("http://www.xx.com");
        postMethod.setParameter("name", "post");
        try {
            int statusCode = httpclient.executeMethod(postMethod);
            System.out.println(statusCode);
            System.out.println(postMethod.getResponseBodyAsString());
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public void testGetMethod() {
        HttpClient httpclient = new HttpClient();
        GetMethod getMethod = new GetMethod("http://www.xx.com?name=get");//GEt方法的参数直接跟在地址后面
        try {
            int statusCode = httpclient.executeMethod(getMethod);
            System.out.println(statusCode);
            System.out.println(getMethod.getResponseBodyAsString());
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public static String httpsPost(String url) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setDefaultRequestConfig(requestConfig).build();
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                StringBuffer sb = new StringBuffer("");
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                String line;
                String separator = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + separator);
                }
                in.close();

                return sb.toString();
            } else {
                System.out.println("状态码：" + state);
            }
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
            }
        }
        return "";
    }

    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            return new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (GeneralSecurityException e) {
        }
        return null;
    }

    public void testFile() {
        Collection<File> files = FileUtils.listFiles(new File("E:/test/"), new IOFileFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return false;
            }

            @Override
            public boolean accept(File file) {
                return true;
            }
        }, TrueFileFilter.INSTANCE);

        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }

    public void testSendMsg() {
        String pushMsg = "test msg";
        msgProducer.sendObjMsg(MsgKey.QUEUE_J_PUSH_KEY, pushMsg);
    }
}

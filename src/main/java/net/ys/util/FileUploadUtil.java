package net.ys.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: NMY
 * Date: 18-5-4
 */
public class FileUploadUtil {

    static CloseableHttpClient httpClient;
    static HttpPost httppost;

    static {
        httpClient = HttpClients.createDefault();
        httppost = new HttpPost("http://localhost:8080/file/mvc/upload.do");
        //10秒连接超时，200秒上传超时
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(200000).build();
        httppost.setConfig(requestConfig);
    }

    public static void main(String[] args) {
        mvcUpload();
        mvcUpload();
        mvcUpload();
        mvcUpload();
    }

    public static boolean mvcUpload() {
        boolean flag = false;
        try {
            FileBody bin = new FileBody(new File("E://test.txt"));
            StringBody projectId = new StringBody("projectId", ContentType.TEXT_PLAIN);
            StringBody fileName = new StringBody("fileName", ContentType.TEXT_PLAIN);
            StringBody fileLen = new StringBody("10000", ContentType.TEXT_PLAIN);
            StringBody startPoint = new StringBody("0", ContentType.TEXT_PLAIN);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("file", bin);
            builder.addPart("projectId", projectId);
            builder.addPart("fileName", fileName);
            builder.addPart("fileLen", fileLen);
            builder.addPart("startPoint", startPoint);
            HttpEntity reqEntity = builder.build();
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpClient.execute(httppost);
            try {
                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(entity);
                    flag = "true".equals(result);
                }
            } catch (Exception e) {
                LogUtil.error(e);
            } finally {
                response.close();
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }

        return flag;
    }

    public static boolean servletUpload() throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/file/servlet/upload");
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.addTextBody("projectId", "projectId");
        entityBuilder.addTextBody("fileName", "fileName");
        entityBuilder.addTextBody("fileLen", "10000");
        entityBuilder.addTextBody("startPoint", "0");
        entityBuilder.addBinaryBody("file", new FileInputStream("e://test.txt"));

        httpPost.setEntity(entityBuilder.build());
        CloseableHttpResponse response = httpClient.execute(httpPost);
        boolean flag = false;
        try {
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(entity);
                flag = "true".equals(result);
            }
        } finally {
            response.close();
        }
        return flag;
    }
}

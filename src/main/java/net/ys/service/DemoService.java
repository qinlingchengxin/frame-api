package net.ys.service;

import net.ys.threadpool.ThreadPoolManager;
import net.ys.utils.LogUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.stereotype.Service;

@Service
public class DemoService {

    public void testThread() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.debug("DemoService-->" + System.currentTimeMillis());
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
}

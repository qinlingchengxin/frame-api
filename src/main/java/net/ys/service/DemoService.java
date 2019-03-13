package net.ys.service;

import net.ys.constant.MsgKey;
import net.ys.threadpool.ThreadPoolManager;
import net.ys.utils.LogUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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

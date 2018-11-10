package net.ys.utils;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

/**
 * java 执行脚本工具类
 */
public class BatUtil {

    /**
     * ping测试 ip或是域名
     *
     * @param address
     * @return
     */
    public static boolean ping(String address) {
        try {
            Process process = Runtime.getRuntime().exec("ping " + address);
            InputStreamReader r = new InputStreamReader(process.getInputStream(), Charset.forName("GBK"));
            LineNumberReader returnData = new LineNumberReader(r);
            String result = "";
            String line;
            int i = 0;
            while ((line = returnData.readLine()) != null && i++ < 5) {
                result += line;
            }
            return result.contains("TTL") || result.contains("ttl");
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * telnet检测ip，端口号是否通
     *
     * @param address
     * @param port
     * @return
     */
    public static boolean telnet(String address, int port) {
        try {
            TelnetClient telnetClient = new TelnetClient("vt200");
            telnetClient.setDefaultTimeout(5000);
            telnetClient.connect(address, port);
            telnetClient.disconnect();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(telnet("10.40.40.171", 6378));
        System.out.println(telnet("10.40.40.171", 6379));
    }
}
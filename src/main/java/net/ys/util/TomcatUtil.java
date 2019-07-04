package net.ys.util;

import java.io.*;

/**
 * User: NMY
 * Date: 19-7-4
 */
public class TomcatUtil {

    /**
     * 重启tomcat,web项目总不能用，只能够杀死，不能启动
     *
     * @param catalinaHome
     */
    public static void restartTomcat(String catalinaHome) {
        try {
            File f = new File(catalinaHome + "\\restart.bat");
            if (!f.exists()) {
                FileWriter fw = new FileWriter(f);
                BufferedWriter bw = new BufferedWriter(fw);
                StringBuffer sb = new StringBuffer("set CATALINA_HOME=");
                sb.append(catalinaHome).append("\r\n").append("call ").append(catalinaHome).append("\\bin\\shutdown.bat\r\n").append("ping 127.0.0.1 -n 5 1>nul\r\n").append("call ").append(catalinaHome).append("\\bin\\startup.bat");
                bw.write(sb.toString());
                bw.close();
                fw.close();
            }

            Runtime run = Runtime.getRuntime();
            Process ps = run.exec(catalinaHome + "\\restart.bat");
            // 我很奇怪 下面的代码去掉的话 tomcat的黑框就不能出现
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), "GBK"));// 注意中文编码问题
            String line;
            while ((line = br.readLine()) != null) {
                LogUtil.debug("StartedLog==>" + line);
            }
            br.close();
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    /**
     * 杀死tomcat进程
     */
    public static void stopTomcat() {
        try {
            int processID = Tools.getProcessID();
            String osType = System.getProperty("os.name");
            if (osType.contains("Windows")) {
                Runtime.getRuntime().exec("TASKKILL /F /PID " + processID);
            } else {
                Runtime.getRuntime().exec("kill -9 " + processID);
            }
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }
}

package net.ys.util;

import java.io.*;

/**
 * User: NMY
 * Date: 19-7-4
 */
public class TomcatUtil {

    public static void main(String[] args) throws IOException {
        restartTomcat("E:\\apache\\apache-tomcat-7.0.52");
    }

    /**
     * 重启tomcat
     *
     * @param catalinaHome
     */
    public static void restartTomcat(String catalinaHome) {
        try {
            File f = new File(catalinaHome + "\\bin\\restart.bat");
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
            Process ps = run.exec(catalinaHome + "\\bin\\restart.bat");
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
}

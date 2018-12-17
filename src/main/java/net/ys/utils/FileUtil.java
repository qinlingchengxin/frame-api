package net.ys.utils;


import net.ys.constant.X;

import java.io.File;

/**
 * User: NMY
 * Date: 18-12-17
 */
public class FileUtil {

    /**
     * 给定路径生成文件对象
     *
     * @param obj
     * @return
     */
    public static File genFile(String root, String... obj) {

        StringBuffer sb = new StringBuffer(root);
        for (String s : obj) {
            sb.append(X.SEPARATOR).append(s);
        }

        File file = new File(sb.toString());

        File parentFile = file.getParentFile();

        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        return file;
    }
}

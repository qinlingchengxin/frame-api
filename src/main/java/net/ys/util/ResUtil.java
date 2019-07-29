package net.ys.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 加载类路径资源
 * User: NMY
 * Date: 19-7-20
 */
public class ResUtil {

    public static InputStream localResStream(String resName) {
        return ResUtil.class.getClassLoader().getResourceAsStream(resName);
    }

    public static InputStream remoteResStream(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        return url.openStream();
    }

    public static String loadResPath(String resName) {
        return ResUtil.class.getClassLoader().getResource(resName).getPath();
    }
}

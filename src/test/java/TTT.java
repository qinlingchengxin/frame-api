import net.ys.utils.req.HttpResponse;
import net.ys.utils.req.HttpUtil;

import java.io.IOException;

/**
 * User: NMY
 * Date: 19-5-10
 */
public class TTT {
    public static void main(String[] args) throws IOException {
        HttpResponse response = HttpUtil.doGet("http://www.baidu.com");
        System.out.println(response);
    }
}

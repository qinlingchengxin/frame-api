package net.ys.constant;

/**
 * User: NMY
 * Date: 17-5-17
 */
public interface X {

    interface Time {
        //对应秒
        int DAY_SECOND = 24 * 60 * 60;
        int HOUR_SECOND = 60 * 60;
        int MINUTE = 60;
        //对应毫秒
        int DAY_MILLISECOND = 24 * 60 * 60 * 1000;
        int HOUR_MILLISECOND = 60 * 60 * 1000;
        int MINUTE_MILLISECOND = 60 * 1000;
        int SECOND_MILLISECOND = 1000;
    }

    /**
     * 编码
     */
    interface Code {
        String U = "UTF-8";
        String I = "ISO-8859-1";
    }

    interface ContentType {
        String JSON = "application/json;charset=UTF-8";
    }
}

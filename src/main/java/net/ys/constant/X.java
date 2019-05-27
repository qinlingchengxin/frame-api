package net.ys.constant;

/**
 * User: NMY
 * Date: 17-5-17
 */
public interface X {

    String SEPARATOR = "/";

    interface Time {
        int MINUTE_SECOND = 60;
        int DAY_MILLISECOND = 24 * 60 * 60 * 1000;
        int HOUR_MILLISECOND = 60 * 60 * 1000;
        int MINUTE_MILLISECOND = 60 * 1000;
        int SECOND_MILLISECOND = 1000;
    }

    /**
     * 短信配置
     */
    interface SMS {
        String URL = "http://msg.jiaoyi365.com/api/sms/sendContent";
        String CONTENT = "尊敬的用户，您好！您操作的验证码为：%s。切勿转发或告知他人。若非您本人操作，请忽略本条短信。";
        String SIGN = "易证通";
        String SEND_MARK_ID = "";
        String SUFFIX = "";
    }

    interface Code {
        String U = "UTF-8";
        String I = "ISO-8859-1";
        String G = "GBK";
    }

    interface ContentType {
        String JSON = "application/json;charset=UTF-8";
    }
}

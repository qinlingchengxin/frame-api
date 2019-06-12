package net.ys.util.req;

/**
 * User: NMY
 * Date: 19-6-4
 */
public class HttpResponse {

    public int code;

    public String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static HttpResponse error(String message) {
        HttpResponse response = new HttpResponse();
        response.setCode(9999);
        response.setValue(message);
        return response;
    }
}

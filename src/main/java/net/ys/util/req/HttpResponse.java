package net.ys.util.req;

/**
 * User: NMY
 * Date: 19-6-4
 */
public class HttpResponse {

    public int code;

    public String value;

    public HttpResponse(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}

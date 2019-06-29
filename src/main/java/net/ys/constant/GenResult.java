package net.ys.constant;

import net.sf.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GenResult {

    SUCCESS(1000, "request success"),

    SUCCESS_SEND(1000, "send sms success"),

    FAILED(1001, "request failed"),

    PARAMS_ERROR(1002, "parameter error"),

    REQUEST_METHOD_ERROR(1003, "request method error"),

    REQUEST_INVALID(1004, "request invalid"),

    INVALID_PHONE(1005, "invalid phone"),

    SMS_SEND_ERROR(1006, "sms send error"),

    SMS_EXCEEDS_LIMIT(1007, "sms exceeds limit"),

    SMS_SEND_FREQUENTLY(1008, "sms send frequently"),

    REQUEST_IP_INVALID(1006, "ip not on the list"),

    UNKNOWN_ERROR(9999, "unknown error"),;

    public int msgCode;
    public String message;

    private GenResult(int msgCode, String message) {
        this.msgCode = msgCode;
        this.message = message;
    }

    public Map<String, Object> genResult(Object... data) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("code", msgCode);
        map.put("msg", message);
        if (data.length > 0) {
            map.put("data", data[0]);
        }
        return map;
    }

    public String toJson(Object... data) {
        JSONObject object = JSONObject.fromObject(genResult(data));
        return object.toString();
    }

    public int getMsgCode() {
        return msgCode;
    }

    public String getMessage() {
        return message;
    }
}

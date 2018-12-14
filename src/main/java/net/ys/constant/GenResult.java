package net.ys.constant;

import net.sf.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GenResult {

    SUCCESS(1000, "request success"),

    FAILED(1001, "request failed"),

    PARAMS_ERROR(1002, "parameter error"),

    REQUEST_METHOD_ERROR(1003, "request method error"),

    UNKNOWN_ERROR(9999, "unknown error"),;

    public int msgCode;
    public String message;

    private GenResult(int msgCode, String message) {
        this.msgCode = msgCode;
        this.message = message;
    }

    public Map<String, Object> genResult() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("code", msgCode);
        map.put("msg", message);
        return map;
    }

    public Map<String, Object> genResult(Object data) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("code", msgCode);
        map.put("msg", message);
        map.put("data", data);
        return map;
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        object.put("code", msgCode);
        object.put("msg", message);
        return object.toString();
    }

    public int getMsgCode() {
        return msgCode;
    }

    public String getMessage() {
        return message;
    }
}

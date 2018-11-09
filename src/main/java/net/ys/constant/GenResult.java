package net.ys.constant;

import net.sf.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GenResult {
    SUCCESS(1000, "请求成功"),

    FAILED(1001, "请求失败"),

    PARAMS_ERROR(1003, "请求参数错误"),

    UNKNOWN_ERROR(9999, "未知异常"),;

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
}

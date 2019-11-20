package net.ys.filter;

import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest req = null;
    private Map params = null;

    public XssRequestWrapper(HttpServletRequest req) {
        super(req);
        this.req = req;
    }

    public String getParameter(String name) {
        String value = super.getParameter(name);
        return encode(value);
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        return encode(value);
    }

    private String encode(String value) {
        if (value != null && "".equals(value) == false) {
            value = HtmlUtils.htmlEscape(value);
        }
        return value;
    }

    private String[] encode(String[] values) {
        if (values != null && values.length > 0) {
            String temps[] = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                String result = values[i];
                if (result != null && "".equals(result) == false) {
                    result = HtmlUtils.htmlEscape(result);
                }
                temps[i] = result;
            }
            return temps;
        }
        return values;
    }

    public Map<String, String[]> getParameterMap() {
        if (params != null) {
            return params;
        }
        Map<String, String[]> map = super.getParameterMap();
        if (map != null) {
            params = new HashMap<String, String[]>();
            Iterator<String> keys = map.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String[] values = map.get(key);
                params.put(key, encode(values));
            }
        }
        return params;
    }

    public String[] getParameterValues(String name) {
        String values[] = super.getParameterValues(name);

        return encode(values);
    }

    public HttpServletRequest getReq() {
        return req;
    }

    public void setReq(HttpServletRequest req) {
        this.req = req;
    }
}
package net.ys.utils.req;

import java.util.Map;

/**
 * User: NMY
 * Date: 19-6-4
 */
public class HttpUtilProxy {

    public static HttpResponse doGet(String address) {
        String protocol = address.substring(0, 5).toLowerCase();
        if ("https:".equals(protocol)) {
            return HttpsUtil.doGet(address);
        }
        return HttpUtil.doGet(address);
    }

    public static HttpResponse doPost(String address, Map<String, Object> params) {
        String protocol = address.substring(0, 5).toLowerCase();
        if ("https:".equals(protocol)) {
            return HttpsUtil.doPost(address, params);
        }
        return HttpUtil.doPost(address, params);
    }

    public static HttpResponse doPostTextXml(String address, String xml) {
        String protocol = address.substring(0, 5).toLowerCase();
        if ("https:".equals(protocol)) {
            return HttpsUtil.doPostTextXml(address, xml);
        }
        return HttpUtil.doPostTextXml(address, xml);
    }

    public static HttpResponse doPostAppXml(String address, String xml) {
        String protocol = address.substring(0, 5).toLowerCase();
        if ("https:".equals(protocol)) {
            return HttpsUtil.doPostAppXml(address, xml);
        }
        return HttpUtil.doPostAppXml(address, xml);
    }

    public static HttpResponse doPostJson(String address, String json) {
        String protocol = address.substring(0, 5).toLowerCase();
        if ("https:".equals(protocol)) {
            return HttpsUtil.doPostJson(address, json);
        }
        return HttpUtil.doPostJson(address, json);
    }

    public static HttpResponse doPostFormData(String address, Map<String, String> params) {
        String protocol = address.substring(0, 5).toLowerCase();
        if ("https:".equals(protocol)) {
            return HttpsUtil.doPostFormData(address, params);
        }
        return HttpUtil.doPostFormData(address, params);
    }
}

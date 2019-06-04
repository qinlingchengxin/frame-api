package net.ys.utils;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 绕过ssl验证
 */
public class HttpsUtil {

    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslsession) {
            return true;
        }
    };

    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {
        private X509Certificate[] certificates;

        @Override
        public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public static String getMethod(String address) {
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(address);
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            connection = (HttpsURLConnection) url.openConnection();
            TrustManager[] tm = {ignoreCertificationTrustManger};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());

            SSLSocketFactory ssf = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(ssf);
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                LogUtil.info("getMethod:url[" + address + "] responseCode:" + responseCode);
                return null;
            }

            InputStream reader = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(2048);
            byte[] bytes = new byte[2048];
            int len;
            while ((len = reader.read(bytes)) > 0) {
                buffer.write(bytes, 0, len);
            }
            reader.close();
            return new String(buffer.toByteArray());
        } catch (Exception e) {
            LogUtil.error(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String urlString = "https://www.hbggzyfwpt.cn/admin/web/admin/login.do";
        String result = HttpsUtil.getMethod(urlString);
        if (result != null) {
            LogUtil.debug(result);
        }
    }
}
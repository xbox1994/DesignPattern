package Other.http.api;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class JnlpFileDownloader {
    static IPMIHTTPResponse get(String uri, Map<String, String> params, Map<String, String> headers) throws IOException {
        return run(buildGetUriRequest(uri, params, headers));
    }

    static IPMIHTTPResponse post(String uri, Map<String, String> params, Map<String, String> headers) throws IOException {
        return run(buildPostUriRequest(uri, params, headers));
    }

    private static IPMIHTTPResponse run(HttpUriRequest httpUriRequest) throws IOException {
        String result;

        CookieStore cookieStore = new BasicCookieStore();
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);
        CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(cm).build();
        HttpResponse httpResponse = client.execute(httpUriRequest);
        InputStream input = httpResponse.getEntity().getContent();
        IPMIHTTPResponse response = new IPMIHTTPResponse();
        response.setCookie(cookieStore.getCookies());
        if (null != input) {
            try {
                result = IOUtils.toString(input, "UTF-8");
                response.setBody(result);
            } finally {
                input.close();
            }
        }
        return response;
    }

    private static HttpUriRequest buildGetUriRequest(String uri, Map<String, String> params, Map<String, String> headers) throws UnsupportedEncodingException {
        String fullUri = buildUrlWithParams(uri, params);
        HttpGet httpGet = new HttpGet(fullUri);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpGet;
    }

    private static HttpUriRequest buildPostUriRequest(String uri, Map<String, String> params, Map<String, String> headers) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(uri);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return httpPost;
    }

    private static String buildUrlWithParams(String uri, Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(uri);
        if (null != params && !params.isEmpty()) {
            if (!uri.contains("?")) {
                urlBuilder.append("?");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String valueStr = null == value ? "" : value;
                if (!urlBuilder.toString().endsWith("?")) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(key).append("=").append(URLEncoder.encode(valueStr, "utf-8"));
            }
        }
        return urlBuilder.toString();
    }

    abstract String download() throws IOException;

    static class IPMIHTTPResponse {
        private String body;
        private List<Cookie> cookie;

        public IPMIHTTPResponse() {
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<Cookie> getCookie() {
            return cookie;
        }

        public void setCookie(List<Cookie> cookie) {
            this.cookie = cookie;
        }

        @Override
        public String toString() {
            return "IPMIHTTPResponse{" +
                    "body='" + body + '\'' +
                    ", cookie='" + cookie + '\'' +
                    '}';
        }
    }
}

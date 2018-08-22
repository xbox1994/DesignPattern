package Other.http;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HTTPUtil {
    public static void main(String[] args) throws IOException {
//        String urlGetCookies = "https://172.30.20.28";
//        IPMIHTTPResponse cookies = get(urlGetCookies, null, null);
//        System.out.println(cookies);

        String loginUrl = "https://172.30.20.28/cgi/login.cgi";
        Map<String, String> params = new HashMap<>();
        params.put("name", "admin");
        params.put("pwd", "admin");

//        params.put("encodedpwd", "YWRtaW4=");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        IPMIHTTPResponse response = get(loginUrl, params, headers);
        System.out.println(response);

//        String url = "https://172.30.30.22/cgi/url_redirect.cgi?url_name=jnlp&url_type=jwsk";
//        Map<String, String> headers1 = new HashMap<>();
//        headers1.put("Cookie", "SID=xqhnxselwckfruuk");
//        IPMIHTTPResponse response1 = get(url, null, headers1);
//        System.out.println(response1);
    }

    public static IPMIHTTPResponse get(String uri, Map<String, String> params, Map<String, String> headers) throws IOException {
        return run(buildGetUriRequest(uri, params), headers);
    }

    public static IPMIHTTPResponse post(String uri, Map<String, String> params, Map<String, String> headers) throws IOException {
        return run(buildPostUriRequest(uri, params), headers);
    }

    public static IPMIHTTPResponse run(HttpUriRequest httpUriRequest, Map<String, String> headers) throws IOException {
        String result;

        CookieStore cookieStore = new BasicCookieStore();
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
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
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpUriRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpResponse httpResponse = client.execute(httpUriRequest);
        InputStream input = httpResponse.getEntity().getContent();
        IPMIHTTPResponse response = new IPMIHTTPResponse();
        response.setCookie(cookieStore.getCookies().toString());
        if (null != input) {
            try {
                result = IOUtils.toString(input, "UTF-8");
                response.setBody(result);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return response;
    }

    private static HttpUriRequest buildGetUriRequest(String uri, Map<String, String> params) throws UnsupportedEncodingException {
        String fullUri = buildUrlWithParams(uri, params);
        return new HttpGet(fullUri);
    }

    private static HttpUriRequest buildPostUriRequest(String uri, Map<String, String> params) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(uri);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        params.forEach((key, value) -> nameValuePairs.add(new BasicNameValuePair(key, value)));
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

}

package Other.http;

import org.apache.http.cookie.Cookie;

import java.util.List;

public class IPMIHTTPResponse {
    private String body;
    private List<Cookie> cookie;

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

    public IPMIHTTPResponse() {
    }

    @Override
    public String toString() {
        return "IPMIHTTPResponse{" +
                "body='" + body + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}

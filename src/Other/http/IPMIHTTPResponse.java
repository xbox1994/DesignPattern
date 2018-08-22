package Other.http;

public class IPMIHTTPResponse {
    private String body;
    private String cookie;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
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

package Other.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static Other.http.HTTPUtil.get;
import static Other.http.HTTPUtil.post;

public class IntelHttpMocker extends ManagementConsoleHttpMocker {

    public IntelHttpMocker(String ip, String username, String password) {
        super(ip, username, password);
    }

    public static void main(String[] args) throws IOException {
        ManagementConsoleHttpMocker httpMocker = new IntelHttpMocker("172.30.20.28", "admin", "admin");
        String jnlpFile = httpMocker.getJnlpFile();
        System.out.println(jnlpFile);
    }

    @Override
    protected String getLoggedSid() throws IOException {
        String loginUrl = "https://" + ip + "/cgi/login.cgi";
        Map<String, String> params = new HashMap<>();
        params.put("name", username);
        params.put("pwd", password);
        params.put("encodedpwd", Base64.encodeBase64String(password.getBytes()));

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        IPMIHTTPResponse response = post(loginUrl, params, headers);
        Map<String, String> cookies = response.getCookie().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        System.out.println(cookies);
        return cookies.get("__Host-SID");
    }

    @Override
    public String getJnlpFile() throws IOException {
        String url = "https://" + ip + "/cgi/url_redirect.cgi?url_name=jnlp&url_type=jwsk&lang_setting=English&host_addr="+ip;
        Map<String, String> header = new HashMap<>();
        String loggedSid = getLoggedSid();
        header.put("Cookie", "__Host-SID=" + loggedSid);
        IPMIHTTPResponse response = get(url, null, header);

        String body = response.getBody();
        if (body.startsWith("<jnlp")) {
            return body;
        }
        logout(loggedSid);
        return null;
    }

    @Override
    protected void logout(String sid) throws IOException {
        String url = "https://" + ip + "/cgi/logout.cgi";
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", "__Host-SID=" + sid);
        get(url, null, header);
    }

}

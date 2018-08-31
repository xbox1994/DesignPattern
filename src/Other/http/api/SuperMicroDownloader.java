package Other.http.api;

import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperMicroDownloader extends JnlpFileDownloader {
    private String ip = "172.30.30.22";
    private String username = "ADMIN";
    private String password = "ADMIN";

    public static void main(String[] args) throws IOException {
        SuperMicroDownloader httpMocker = new SuperMicroDownloader();
        String jnlpFile = httpMocker.download();
        System.out.println(jnlpFile);
    }

    @Override
    public String download() throws IOException {
        // 1. 登录成功拿到已授权的Cookie
        String loginUrl = "https://" + ip + "/cgi/login.cgi";
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("name", username);
        loginParams.put("pwd", password);
        Map<String, String> loginHeaders = new HashMap<>();
        loginHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        IPMIHTTPResponse cookieResponse = post(loginUrl, loginParams, loginHeaders);
        Map<String, String> cookies = cookieResponse.getCookie().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
        String sid = cookies.get("SID");
        if (sid == null || sid.equals("")) {
            return null;
        }

        // 2. 下载Jnlp文件
        String jnlpDownloadUrl = "https://" + ip + "/cgi/url_redirect.cgi?url_name=jnlp&url_type=jwsk";
        Map<String, String> jnlpDownloadHeader = new HashMap<>();
        jnlpDownloadHeader.put("Cookie", "SID=" + sid);
        IPMIHTTPResponse jnlpResponse = get(jnlpDownloadUrl, null, jnlpDownloadHeader);
        String jnlpFile = jnlpResponse.getBody();
        if (!jnlpFile.startsWith("<jnlp")) {
            return null;
        }

        // 3. 登出用户
        String logoutUrl = "https://" + ip + "/cgi/logout.cgi";
        Map<String, String> logoutHeader = new HashMap<>();
        logoutHeader.put("Cookie", "SID=" + sid);
        get(logoutUrl, null, logoutHeader);

        return jnlpFile;
    }
}

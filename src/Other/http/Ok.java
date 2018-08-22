package Other.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Ok {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://172.30.20.28/cgi/url_redirect.cgi?url_name=jnlp&url_type=jwsk")
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "fcaad3ee-bba0-6d11-8248-759146e8eec0")
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package Other.http;

import java.io.IOException;

public abstract class ManagementConsoleHttpMocker {
    protected String ip;
    protected String username;
    protected String password;

    public ManagementConsoleHttpMocker(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    protected abstract String getLoggedSid() throws IOException;
    public abstract String getJnlpFile() throws IOException;
    protected abstract void logout(String sid) throws IOException;
}

package Other.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Optional;

public abstract class SSHManager {
    protected static final String IDENTIFY_NONE = "IDENTIFY_NONE";
    protected static final String IDENTIFY_PASSWORD = "IDENTIFY_PASSWORD";
    protected static final String IDENTIFY_KEY = "IDENTIFY_KEY";
    protected static final int TIMEOUT = 5 * 1000;
    protected String username;
    protected String ip;
    protected int port;
    protected String password;
    protected String identifyKey;
    protected String identifyType;
    protected int timeout = TIMEOUT;
    protected JSch jSch;

    public SSHManager(String username, String identifyString, String ip, int port, String identifyType) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.identifyType = identifyType;
        switch (identifyType) {
            case IDENTIFY_PASSWORD:
                this.password = identifyString;
                break;
            case IDENTIFY_KEY:
                this.identifyKey = identifyString;
                break;
            case IDENTIFY_NONE:
                break;
        }
    }

    public abstract Optional<SSHOutput> sendCommand(String command);

}

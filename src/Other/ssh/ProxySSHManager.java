package Other.ssh;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.util.Optional;

public class ProxySSHManager extends SSHManager {
    protected String usernameProxy;
    protected String ipProxy;
    protected int portProxy;
    protected String identifyTypeProxy;
    protected String identifyStringProxy;

    public ProxySSHManager(String username, String identifyString, String ip, int port, String identifyType,
                           String usernameProxy, String identifyStringProxy, String ipProxy, int portProxy, String identifyTypeProxy) {
        super(username, identifyString, ip, port, identifyType);
        this.usernameProxy = usernameProxy;
        this.ipProxy = ipProxy;
        this.portProxy = portProxy;
        this.identifyTypeProxy = identifyTypeProxy;
        this.identifyStringProxy = identifyStringProxy;
    }

    public Optional<SSHOutput> sendCommand(String command) {
        Session sessionProxy = null;
        try {
            sessionProxy = createBaseSession(usernameProxy, identifyTypeProxy, identifyStringProxy, ipProxy, portProxy);
            sessionProxy.connect(timeout);
            String sshCommand = "ssh" +
                    " -o StrictHostKeyChecking=no" +
                    " -o ServerAliveInterval=" + SSH_KEEP_ALIVE_INTERVAL_SECOND +
                    " -o ServerAliveCountMax=" + SSH_KEEP_ALIVE_COUNT_MAX_SECOND +
                    " -p" + port +
                    " " + username + "@" + ip + " ";
            return getSSHOutput(sshCommand + command, sessionProxy);
        } catch (IOException | JSchException e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (sessionProxy != null) {
                sessionProxy.disconnect();
            }
        }
    }
}
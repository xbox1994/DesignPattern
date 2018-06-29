package Other.ssh;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.Random;


/**
 * Warning: You must make sure you have configured ssh no password login between proxy and destination by yourself.
 * <p>
 * This manager will build the ssh tunnel between local server and proxy, and connect the local server port to ssh into destination.
 */

public class TunnelSSHManager extends SSHManager {
    protected String usernameProxy;
    protected String ipProxy;
    protected int portProxy;
    protected String identifyTypeProxy;
    protected String identifyStringProxy;

    public TunnelSSHManager(String username, String identifyString, String ip, int port, String identifyType,
                            String usernameProxy, String identifyStringProxy, String ipProxy, int portProxy, String identifyTypeProxy) {
        super(username, identifyString, ip, port, identifyType);
        this.usernameProxy = usernameProxy;
        this.ipProxy = ipProxy;
        this.portProxy = portProxy;
        this.identifyTypeProxy = identifyTypeProxy;
        this.identifyStringProxy = identifyStringProxy;
    }

    private static int getLocalAvailablePort() {
        int port;
        do {
            port = new Random().nextInt(20000) + 10000;
        } while (!isPortAvailable(port));

        return port;
    }

    private static boolean isPortAvailable(final int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            return true;
        } catch (final IOException ignored) {
        }
        return false;
    }

    public Optional<SSHOutput> sendCommand(String command) {
        try {
            // build server local ssh tunnel between server and proxy
            int serverLocalSSHTunnelPort = getLocalAvailablePort();
            Session sessionProxy = createBaseSession(usernameProxy, identifyTypeProxy, identifyStringProxy, ipProxy, portProxy);
            sessionProxy.setPortForwardingL(serverLocalSSHTunnelPort, ip, port);
            sessionProxy.connect(timeout);

            // ssh into local ssh tunnel to connect destination
            Session sessionDestination = createBaseSession(username, identifyType, identifyString, "127.0.0.1", serverLocalSSHTunnelPort);
            sessionDestination.connect(timeout);

            Optional<SSHOutput> sshOutput = getSSHOutput(command, sessionDestination);
            sessionProxy.disconnect();
            return sshOutput;
        } catch (IOException | JSchException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
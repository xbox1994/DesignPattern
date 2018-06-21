package Other.ssh;


import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

public class ProxySSHManager extends SSHManager {
    protected String usernameProxy;
    protected String passwordProxy;
    protected String identifyKeyProxy;
    protected String ipProxy;
    protected int portProxy;
    protected String identifyTypeProxy;

    public ProxySSHManager(String username, String identifyString, String ip, int port, String identifyType,
                           String usernameProxy, String identifyStringProxy, String ipProxy, int portProxy, String identifyTypeProxy) {
        super(username, identifyString, ip, port, identifyType);
        this.usernameProxy = usernameProxy;
        this.ipProxy = ipProxy;
        this.portProxy = portProxy;
        this.identifyTypeProxy = identifyTypeProxy;
        switch (identifyTypeProxy) {
            case IDENTIFY_PASSWORD:
                this.passwordProxy = identifyStringProxy;
                break;
            case IDENTIFY_KEY:
                this.identifyKeyProxy = identifyStringProxy;
                break;
            case IDENTIFY_NONE:
                break;
        }
    }

    private static int getAvailablePort() {
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
        String stdout;
        String stderr;
        try {
            jSch = new JSch();
            if (identifyTypeProxy.equals(IDENTIFY_KEY)) {
                jSch.addIdentity("identifyKeyName", identifyKeyProxy.getBytes(), null, null);
            }
            Session sessionProxy = jSch.getSession(usernameProxy, ipProxy, portProxy);
            if (identifyTypeProxy.equals(IDENTIFY_PASSWORD)) {
                sessionProxy.setPassword(passwordProxy);
            }
            int availablePort = getAvailablePort();
            sessionProxy.setPortForwardingL(availablePort, ip, port);
            sessionProxy.setConfig("StrictHostKeyChecking", "no");
            sessionProxy.connect(timeout);

            jSch.removeAllIdentity();
            if (identifyType.equals(IDENTIFY_KEY)) {
                jSch.addIdentity("identifyKeyProxyName", identifyKey.getBytes(), null, null);
            }
            Session sessionDestination = jSch.getSession(username, "127.0.0.1", availablePort);
            if (identifyType.equals(IDENTIFY_PASSWORD)) {
                sessionDestination.setPassword(password);
            }
            sessionDestination.setConfig("StrictHostKeyChecking", "no");
            sessionDestination.connect(timeout);

            Channel execChannel = sessionDestination.openChannel("exec");
            ((ChannelExec) execChannel).setCommand(command);
            InputStream commandOutput = execChannel.getInputStream();
            InputStream commandErrOutput = execChannel.getExtInputStream();
            execChannel.connect();

            stdout = IOUtils.toString(commandOutput, StandardCharsets.UTF_8);
            stderr = IOUtils.toString(commandErrOutput, StandardCharsets.UTF_8);

            execChannel.disconnect();
            sessionDestination.disconnect();
            sessionProxy.disconnect();
        } catch (IOException | JSchException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(new SSHOutput(stdout, stderr));
    }
}
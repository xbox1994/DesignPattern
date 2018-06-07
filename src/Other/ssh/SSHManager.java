package Other.ssh;


import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SSHManager {
    private static final int PORT = 22;
    private static final int TIMEOUT = 10 * 1000;
    private String username;
    private String ip;
    private int port;
    private String password;
    private Session session;
    private int timeout;

    public SSHManager(String username, String ip) {
        this(username, ip, 22, "", 10000);
    }

    public SSHManager(String username, String password, String ip) {
        this(username, ip, PORT, password, TIMEOUT);
    }

    public SSHManager(String username, String ip, int port, String password, int timeout) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
    }

    private void connect() throws JSchException {
        JSch jSch = new JSch();
//            jSch.addIdentity("~/.ssh/id_rsa");
        session = jSch.getSession(username, ip, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(timeout);
    }

    public Optional<SSHOutput> sendCommand(String command) {
        String stdout;
        String stderr;
        try {
            connect();

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            InputStream commandErrOutput = channel.getExtInputStream();
            channel.connect();

            stdout = IOUtils.toString(commandOutput, StandardCharsets.UTF_8);
            stderr = IOUtils.toString(commandErrOutput, StandardCharsets.UTF_8);
            channel.disconnect();
        } catch (IOException | JSchException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(new SSHOutput(stdout, stderr));
    }

    public void close() {
        session.disconnect();
    }

}
package Other.ssh;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class SSHManager {
    protected static final String PASSWORD = "PASSWORD";
    protected static final String IDENTITY_KEY_STRING = "IDENTITY_KEY_STRING";
    protected static final String IDENTITY_KEY_FILE_PATH = "IDENTITY_KEY_FILE_PATH";
    protected static final int TIMEOUT = 10 * 1000;
    protected static final String SSH_KEEP_ALIVE_INTERVAL_SECOND = "60";
    protected static final String SSH_KEEP_ALIVE_COUNT_MAX_SECOND = "10";

    protected String username;
    protected String ip;
    protected int port;
    protected String identifyString;
    protected String identifyType;
    protected int timeout = TIMEOUT;
    protected JSch jSch;

    public SSHManager(String username, String identifyString, String ip, int port, String identifyType) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.identifyType = identifyType;
        this.identifyString = identifyString;
        jSch = new JSch();
    }

    public abstract Optional<SSHOutput> sendCommand(String command);

    protected Session createBaseSession() throws JSchException {
        return createBaseSession(username, identifyType, identifyString, ip, port);
    }

    protected Session createBaseSession(String username,
                                        String identifyType,
                                        String identifyString,
                                        String ip,
                                        int port) throws JSchException {
        jSch.removeAllIdentity();
        Session baseSession = jSch.getSession(username, ip, port);
        addSessionIdentity(baseSession, identifyType, identifyString);
        return baseSession;
    }

    protected void addSessionIdentity(Session baseSession, String identifyType, String identifyString) throws JSchException {
        if (identifyType.equals(IDENTITY_KEY_STRING)) {
            jSch.addIdentity("identifyKeyName", identifyString.getBytes(), null, null);
        } else if (identifyType.equals(IDENTITY_KEY_FILE_PATH)) {
            jSch.addIdentity(identifyString);
        }
        if (identifyType.equals(PASSWORD)) {
            baseSession.setPassword(identifyString);
        }
        baseSession.setConfig("StrictHostKeyChecking", "no");
        baseSession.setConfig("ServerAliveInterval", SSH_KEEP_ALIVE_INTERVAL_SECOND);
        baseSession.setConfig("ServerAliveCountMax", SSH_KEEP_ALIVE_COUNT_MAX_SECOND);
    }

    protected Optional<SSHOutput> getSSHOutput(String command, Session session) throws JSchException, IOException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        InputStream commandOutput = channel.getInputStream();
        InputStream commandErrOutput = channel.getExtInputStream();
        channel.connect();
        String stdout = IOUtils.toString(commandOutput, StandardCharsets.UTF_8);
        String stderr = IOUtils.toString(commandErrOutput, StandardCharsets.UTF_8);
        channel.disconnect();
        session.disconnect();
        return Optional.of(new SSHOutput(stdout, stderr));
    }

    protected Optional<SSHOutput> getSSHOutput(String command, Channel channel) throws JSchException, IOException {
        ((ChannelExec) channel).setCommand(command);
        InputStream commandOutput = channel.getInputStream();
        InputStream commandErrOutput = channel.getExtInputStream();
        channel.connect();
        String stdout = IOUtils.toString(commandOutput, StandardCharsets.UTF_8);
        String stderr = IOUtils.toString(commandErrOutput, StandardCharsets.UTF_8);
        channel.disconnect();
        return Optional.of(new SSHOutput(stdout, stderr));
    }
}

package Other;


import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSHManager {
    private static final Logger LOGGER =
            Logger.getLogger(SSHManager.class.getName());
    private String username;
    private String ip;
    private int port;
    private String password;
    private Session session;
    private int timeout;

    public SSHManager(String username, String ip) {
        this(username, ip, 22, "", 10000);
    }

    public SSHManager(String username, String ip, int port, String password, int timeout) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
    }

    public String connect() {
        String errorMessage = null;

        try {
            JSch jSch = new JSch();
            jSch.addIdentity("~/.ssh/id_rsa");
            session = jSch.getSession(username, ip, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(timeout);
        } catch (JSchException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    private void logError(String errorMessage) {
        if (errorMessage != null) {
            LOGGER.log(Level.SEVERE, "{0}:{1} - {2}", new Object[]{ip, port, errorMessage});
        }
    }

    public String sendCommand(String command) {
        StringBuilder outputBuffer = new StringBuilder();

        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while (readByte != 0xffffffff) {
                outputBuffer.append((char) readByte);
                readByte = commandOutput.read();
            }

            channel.disconnect();
        } catch (IOException | JSchException ioX) {
            logError(ioX.getMessage());
            return null;
        }

        return outputBuffer.toString();
    }

    public void close() {
        session.disconnect();
    }

}
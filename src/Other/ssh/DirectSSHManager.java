package Other.ssh;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DirectSSHManager extends SSHManager {

    public DirectSSHManager(String username, String identifyString, String ip, int port, String identifyType) {
        super(username, identifyString, ip, port, identifyType);
    }

    public Optional<SSHOutput> sendCommand(String command) {
        String stdout;
        String stderr;
        try {
            jSch = new JSch();
            if (identifyType.equals(IDENTIFY_KEY)) {
                jSch.addIdentity("identifyKeyName", identifyKey.getBytes(), null, null);
            }
            Session sessionProxy = jSch.getSession(username, ip, port);
            if (identifyType.equals(IDENTIFY_PASSWORD)) {
                sessionProxy.setPassword(password);
            }
            sessionProxy.setConfig("StrictHostKeyChecking", "no");
            sessionProxy.connect(timeout);

            Channel channel = sessionProxy.openChannel("exec");
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


}
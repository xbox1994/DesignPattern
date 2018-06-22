package Other.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.util.Optional;

public class DirectSSHManager extends SSHManager {

    public DirectSSHManager(String username, String identifyString, String ip, int port, String identifyType) {
        super(username, identifyString, ip, port, identifyType);
    }

    public Optional<SSHOutput> sendCommand(String command) {
        try {
            Session session = createBaseSession();
            session.connect(timeout);
            return getSSHOutput(command, session);
        } catch (IOException | JSchException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }

}
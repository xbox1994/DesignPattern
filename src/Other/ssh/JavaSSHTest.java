package Other.ssh;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaSSHTest {

    public static void main(String[] args) throws Exception {
        String command = new String(Files.readAllBytes(Paths.get("test.sh")), StandardCharsets.UTF_8);
        System.out.println(command);
        String userName = "ubuntu";
        String password = "ubuntu";
        String connectionIP = "13.250.14.139";
        SSHManager instance = new SSHManager(userName, password, connectionIP);
        SSHOutput output = instance.sendCommand(command).orElseThrow(Exception::new);
        System.out.println(output.getStdout());
        System.err.println(output.getStderr());
        instance.close();
    }
}

package Other;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaSSHTest {

    public static void main(String[] args) throws IOException {
        String command = new String(Files.readAllBytes(Paths.get("test.sh")), StandardCharsets.UTF_8);;
        System.out.println(command);
        String userName = "ubuntu";
        String connectionIP = "13.250.14.139";
        SSHManager instance = new SSHManager(userName, connectionIP);
        String errorMessage = instance.connect();

        if (errorMessage != null) {
            System.out.println(errorMessage);
        }

        String result = instance.sendCommand(command);
        System.out.println(result);
        instance.close();
    }
}

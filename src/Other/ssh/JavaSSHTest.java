package Other.ssh;

public class JavaSSHTest {

    public static void main(String[] args) throws Exception {
        String command = args[3];
        System.out.println("command: " + command);

        String userName = args[0];
        String password = args[2];
        String connectionIP = args[1];
        SSHManager instance = new SSHManager(userName, password, connectionIP);
        SSHOutput output = instance.sendCommand(command).orElseThrow(Exception::new);
        System.out.println("stdout: " + output.getStdout());
        System.err.println("stderr: " + output.getStderr());
        instance.close();
    }
}

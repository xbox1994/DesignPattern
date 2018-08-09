package Other.ssh;

public class JavaSSHTest {

    public static void main(String[] args) throws Exception {
//        String command = args[3];
//        System.out.println("command: " + command);
//
//        String userName = args[0];
//        String password = args[2];
//        String connectionIP = args[1];


//        SSHManager instance = hotswap DirectSSHManager("ubuntu", "~/.ssh/id_rsa", "172.31.30.122", 22, SSHManager.IDENTITY_KEY_FILE_PATH);
        SSHManager instance = new ProxySSHManager("root", "~/.ssh/id_rsa", "10.120.37.5", 22, SSHManager.IDENTITY_KEY_FILE_PATH,
                "root", "~/.ssh/id_rsa", "139.198.188.74", 22, SSHManager.IDENTITY_KEY_FILE_PATH);
        SSHOutput output = instance.sendCommand("ifconfig").orElseThrow(Exception::new);
        System.out.println("stdout: " + output.getStdout());
        System.err.println("stderr: " + output.getStderr());
    }
}

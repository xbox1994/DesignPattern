package Other.ssh;

public class SSHOutput {
    private String stdout;
    private String stderr;

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public SSHOutput(String stdout, String stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }
}

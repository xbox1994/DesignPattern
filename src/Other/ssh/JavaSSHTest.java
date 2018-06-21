package Other.ssh;

public class JavaSSHTest {

    public static void main(String[] args) throws Exception {
//        String command = args[3];
//        System.out.println("command: " + command);
//
//        String userName = args[0];
//        String password = args[2];
//        String connectionIP = args[1];
//        SSHManager instance = new DirectSSHManager("ubuntu", "ubuntu", "13.250.14.139", 22, SSHManager.IDENTIFY_KEY);
        SSHManager instance = new ProxySSHManager("ubuntu", "zhu88jie", "11.22.33.44", 22, SSHManager.IDENTIFY_PASSWORD,
                "ubuntu", "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEogIBAAKCAQEAsMVVEhnEz+Ljb98F4chaxK0qYuJNf+GQl1R/SsDGYiUHZGXC\n" +
                "PYOK0Ir/+FGCDtjxS/u247j8SE7ypNkLJckkD/1RlKw73djwc6gOH3JVIurt01+J\n" +
                "fuhszsr/FmaXLmNEh3d2GQVCDquHNtjz0zJ2lSI5CfSnrfeo+zKztEfz5HNDtFhp\n" +
                "OXO4pQ4G0nQyki4GPJQMQ64W4NpbhvsGz7WAqwpuBPHuFX/+psc8MQni4wL2MS5K\n" +
                "RPYCudHjm+1RvwmIds2rSxrm9jye35JbQyasLL5iILVVoMuX0wsQdhnZ16qbIMmg\n" +
                "jppH9jo1LEo56fSzx6lMC8+g3LjbmI9jwxK6hwIDAQABAoIBAHEbctmIH4SdOsYE\n" +
                "QDE+D8Y0vyBSiSBAZlJOQLzGKKn344j8C5Nsrc/OgQaUsNFrQspJn7aKUPK7gUq1\n" +
                "jgp5b1fD0QV+RzgmbYSzjYXVlGApTk+nm8TWV3jiLuJYdne6EMlVSP2tfZ0BaB1S\n" +
                "KIGEkpfONA0IvJn2E+vXf2DlgUs03UCUBmchjyky+LYAv9ZIi9L9IHlwc6LlKX34\n" +
                "q0jGa3fY+3CbMxmkFt1TM/Aer2T++wXhce+e5DaVgp8tCiYtStEizlifikJd3zbG\n" +
                "JHQOoZngD3gLKvRtJ4uHa4nFtE2cgaE+NZrrZM+36Q2DstGHGobSXV6HGkjL24Dm\n" +
                "3dbfhEECgYEA3Fc6hC3mO+h84ObM3BMvBFj02UGrGavViedchSUWN8VfEi+Yza5p\n" +
                "RbkMMkO6FWCX9zPsfD8CbifHKr86JPM/WDb/vim/R+/wRUnoV84LFrf+/A/Q98uY\n" +
                "7REdXBClnjQd3gW9AKwca9UblV1M+pfrG/jC1pG5lPe6GaWsBBirXFcCgYEAzWD9\n" +
                "3N6rkfCtUP2B6o5tQo3qnAHrqX1Ljyvufx45oChw9tmLW+IOphF65+o+SO05zZ7P\n" +
                "/MeduRfAaCCsVFkSO0verl0pWT84CR53ETqOSJGWxNlVjmSi6R7NMi9oDzlrHi+s\n" +
                "I3xdKf7tnCYvonSXXwvCjJJ3R32Q9Ev61g1XtVECgYAZ72KB4G0zLABW8JO4a8Gi\n" +
                "/12CnQoosWMnIkZvnZfp5J92IogX3amifU+EMxiaH46DmGcDkN8WXDRDQdL0OnWA\n" +
                "Sqj7lypeq01auoJQo34GpI2s2ae9IwfTD6qnBYaZAoVpdr9ybs/BmYnvoNXIwUz5\n" +
                "M+SrIOf5MteC4U9IBCdXbQKBgHC1r9wMn1+iE5uE5E1TCIcj1wLy0t/bESJrW6GL\n" +
                "dth8GTJ/RMmSD7P3l9m6ZaAkADrl0wLIkbhIXqFGfQx1LVo5UuWh3tVJqnjmrTVZ\n" +
                "S0hsDa/ODfPtKrKA4t+NwK/KNQ0F4JRA5efsLX4qPq4LCh9xFr2Ki8dCE9Dd+bSK\n" +
                "8f1hAoGAF+gb1dIG1Ksz99ocCmA2TKkjf3K6LbVtatH0Gx3zgsyhIX67wpSOUGYK\n" +
                "0fTP34qE9BYsC7Ukd5U68UdYZNzPdRxFKT40TQ1cu4iTkwDc6cQie//OqgFMXloW\n" +
                "HlE42Xi0cdbjNPHBTM0XDyBaa7X4m3sRWPUXTcQwtwKCt8XVq14=\n" +
                "-----END RSA PRIVATE KEY-----", "172.31.30.122", 22, SSHManager.IDENTIFY_KEY);
        SSHOutput output = instance.sendCommand("ifconfig").orElseThrow(Exception::new);
        System.out.println("stdout: " + output.getStdout());
        System.err.println("stderr: " + output.getStderr());
    }
}

package Other.http.api;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.io.FileUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class JnlpFileDownloaderLauncher {
    public static void main(String[] args) throws Exception {
        String downloaderString = "\n" +
                "import org.apache.http.cookie.Cookie;\n" +
                "\n" +
                "import java.io.IOException;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "public class SuperMicroDownloader extends JnlpFileDownloader {\n" +
                "    private String ip = \"172.30.30.22\";\n" +
                "    private String username = \"ADMIN\";\n" +
                "    private String password = \"ADMIN\";\n" +
                "\n" +
                "    public static void main(String[] args) throws IOException {\n" +
                "        SuperMicroDownloader httpMocker = new SuperMicroDownloader();\n" +
                "        String jnlpFile = httpMocker.download();\n" +
                "        System.out.println(jnlpFile);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public String download() throws IOException {\n" +
                "        // 1. 登录成功拿到已授权的Cookie\n" +
                "        String loginUrl = \"https://\" + ip + \"/cgi/login.cgi\";\n" +
                "        Map<String, String> loginParams = new HashMap<>();\n" +
                "        loginParams.put(\"name\", username);\n" +
                "        loginParams.put(\"pwd\", password);\n" +
                "        Map<String, String> loginHeaders = new HashMap<>();\n" +
                "        loginHeaders.put(\"Content-Type\", \"application/x-www-form-urlencoded\");\n" +
                "        IPMIHTTPResponse cookieResponse = post(loginUrl, loginParams, loginHeaders);\n" +
                "        Map<String, String> cookies = cookieResponse.getCookie().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));\n" +
                "        String sid = cookies.get(\"SID\");\n" +
                "        if (sid == null || sid.equals(\"\")) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        // 2. 下载Jnlp文件\n" +
                "        String jnlpDownloadUrl = \"https://\" + ip + \"/cgi/url_redirect.cgi?url_name=jnlp&url_type=jwsk\";\n" +
                "        Map<String, String> jnlpDownloadHeader = new HashMap<>();\n" +
                "        jnlpDownloadHeader.put(\"Cookie\", \"SID=\" + sid);\n" +
                "        IPMIHTTPResponse jnlpResponse = get(jnlpDownloadUrl, null, jnlpDownloadHeader);\n" +
                "        String jnlpFile = jnlpResponse.getBody();\n" +
                "        if (!jnlpFile.startsWith(\"<jnlp\")) {\n" +
                "            return null;\n" +
                "        }\n" +
                "\n" +
                "        // 3. 登出用户\n" +
                "        String logoutUrl = \"https://\" + ip + \"/cgi/logout.cgi\";\n" +
                "        Map<String, String> logoutHeader = new HashMap<>();\n" +
                "        logoutHeader.put(\"Cookie\", \"SID=\" + sid);\n" +
                "        get(logoutUrl, null, logoutHeader);\n" +
                "\n" +
                "        return jnlpFile;\n" +
                "    }\n" +
                "}\n";

        String physicalMachineId = "pm-1";
        String destinationPackage = "Other.http.api";
        String jnlpFile = downloadJnlpFileFromCustomDownloader(physicalMachineId, downloaderString, destinationPackage);
        System.out.println(jnlpFile);
    }

    private static String downloadJnlpFileFromCustomDownloader(String physicalMachineId, String downloaderString, String destinationPackage) throws Exception {
        File tempFile = null;
        try {
            String executableFileString = resetPackage(downloaderString, destinationPackage);
            JavaFileInfo info = parseJavaFileInfo(executableFileString);
            if (info == null) {
                throw new Exception("no public class found!");
            }

            tempFile = new File(physicalMachineId + "/" + info.getClassName() + ".java");
            FileUtils.writeStringToFile(tempFile, executableFileString, "UTF-8");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, tempFile.getPath());
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempFile.toURI().toURL()});
            Class<?> cls = Class.forName(info.getPackagePrefix() == null ? "" : info.getPackagePrefix() + info.getClassName(), true, classLoader);
            JnlpFileDownloader downloader = (JnlpFileDownloader) cls.newInstance();

            return downloader.download();
        } finally {
            try {
                if (tempFile == null) {
                    FileUtils.deleteDirectory(tempFile.getParentFile());
                }
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    private static String resetPackage(String fileString, String destinationPackage) {
        String regex = "package .*;\n";
        Pattern pattern = Pattern.compile(regex);
        if (pattern.matcher(fileString).find()) {
            return fileString.replaceFirst(regex, "package " + destinationPackage + ";\n");
        }
        return "package " + destinationPackage + ";\n" + fileString;
    }

    private static JavaFileInfo parseJavaFileInfo(String fileString) {
        JavaFileInfo info = new JavaFileInfo();
        InputStream fin = new ByteArrayInputStream(fileString.getBytes(StandardCharsets.UTF_8));
        CompilationUnit cu = JavaParser.parse(fin);
        cu.getPackageDeclaration().ifPresent((packageName -> {
            String packagePrefix = packageName.getNameAsString();
            if (!packagePrefix.isEmpty()) packagePrefix += ".";
            info.setPackagePrefix(packagePrefix);
        }));
        for (TypeDeclaration type : cu.getTypes())
            if (type instanceof ClassOrInterfaceDeclaration && AccessSpecifier.PUBLIC == Modifier.getAccessSpecifier(type.getModifiers()))
                info.setClassName(type.getName().toString());
        return info;
    }

}

package io.github.betterclient.client.util.autoupdater;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.Util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.jar.JarFile;

public class AutoUpdaterUtil {
    public static void update() {
        IBridge.getPreLaunch().info("Updating!");
        try {
            String command = getJava() + " -jar \"" + downloadUpdater() + "\" \"" + IBridge.getInstance().getVersion() + "\"";
            IBridge.getPreLaunch().info("Command: \"" + command + "\"");

            Runtime.getRuntime().exec(command.split(" "));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    private static String getJava() throws IOException {
        String filePath = new File("").getAbsolutePath();
        File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)) + File.separator + "instance.cfg");
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            if(line.startsWith("JavaPath=")) {
                return line.split("=")[1];
            }
        }

        return "java";
    }

    private static String downloadUpdater() throws IOException, URISyntaxException {
        JarFile file = new JarFile(Util.urlToFile("https://nightly.link/Ascend-Client/Ascend-Client/workflows/updater/modern/Updater.zip"));
        byte[] bites = Util.readAndClose(file.getInputStream(file.getEntry("Updater.jar")));

        File f0 = File.createTempFile("updater", ".jar");
        f0.deleteOnExit();
        Files.write(f0.toPath(), bites);
        return f0.getAbsolutePath();
    }
}

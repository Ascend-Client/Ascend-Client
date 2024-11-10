package io.github.betterclient.client.util.autoupdater;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.Util;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarFile;

public class PreLaunchUpdateChecker {
    public static void check() throws IOException {
        if(Application.isDev) return;
        String commitId = "";
        List<String> text = Files.readAllLines(toPath(PreLaunchUpdateChecker.class.getResourceAsStream("/ascend/github/github.txt")));

        for(String line : text) {
            if(line.startsWith("git.commit.id.abbrev=")) {
                commitId = line.replace("git.commit.id.abbrev=", "");
            }
        }

        if (checkUpdate(commitId) && JOptionPane.showConfirmDialog(null, "There is an update, would you like to update?") == 0) {
            update();
        }
    }

    private static boolean checkUpdate(String commitId) {
        try {
            URL url = new URI("https://api.github.com/repos/Ascend-Client/Ascend-Client/commits").toURL();
            InputStream is = url.openStream();
            String readStr = new String(is.readAllBytes());
            is.close();

            if (readStr.contains("API rate limit exceeded for")) {
                JOptionPane.showConfirmDialog(null, "You are being rate limited by github.", "Update Failed", JOptionPane.OK_CANCEL_OPTION);
                return false;
            }

            JsonArray array = new JsonParser().parse(readStr).getAsJsonArray();
            String version = array.get(0).getAsJsonObject().get("sha").getAsString();
            return !version.startsWith(commitId);
        } catch (IOException | URISyntaxException ex) {
            if (ex.toString().contains("403")) {
                JOptionPane.showConfirmDialog(null, "You are being rate limited by github.", "Update Failed", JOptionPane.OK_CANCEL_OPTION);
                return false;
            }

            IBridge.getPreLaunch().error(ex.toString());
        }

        return false;
    }

    private static Path toPath(InputStream is) {
        try {
            File gitFile = File.createTempFile("git", ".txt");
            gitFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(gitFile);

            fos.write(is.readAllBytes());

            fos.close();
            is.close();

            return gitFile.toPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void update() {
        IBridge.getPreLaunch().info("Updating!");
        try {
            String command = getJava() + " -jar \"" + downloadUpdater() + "\" \"" + IBridge.getPreLaunch().getVersion().version().goodName() + "\"";
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

package io.github.betterclient.fabric;

import io.github.betterclient.client.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Util {
    public static List<JarEntry> getEntries(JarFile file) {
        List<JarEntry> list = new ArrayList<>();
        Iterator<JarEntry> iterator = file.entries().asIterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public static byte[] readAndClose(InputStream str) throws IOException {
        try (str) { return str.readAllBytes(); }
    }

    public static File urlToFile(String url) throws IOException, URISyntaxException {
        return Files.write(
                File.createTempFile(
                        url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf(".")),
                        url.substring(url.lastIndexOf(".") + 1)
                ).toPath(),

                readAndClose(new URI(url).toURL().openConnection().getInputStream())
        ).toFile();
    }

    public static File urlToFile(String url, File f) throws IOException, URISyntaxException {
        return Files.write(
                f.toPath(),
                readAndClose(new URI(url).toURL().openConnection().getInputStream())
        ).toFile();
    }

    public static File download(URL url) throws IOException {
        return Files.write(
                File.createTempFile(
                        url.getFile().substring(url.getFile().lastIndexOf("/") + 1, url.getFile().lastIndexOf(".")),
                        url.getFile().substring(url.getFile().lastIndexOf("."))
                ).toPath(),
                readAndClose(url.openConnection().getInputStream())
        ).toFile();
    }

    @SuppressWarnings("all") //sacrifice the warning for the 1 liner
    public static File downloadIfFirstLaunch(File folder, String url) throws IOException {
        return ((Object) (new File(folder, url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + url.substring(url.lastIndexOf(".")))) instanceof File f) ?
                (f.exists() ? f :
                        Files.write(
                                f.toPath(),
                                readAndClose(new URL(url).openConnection().getInputStream())
                        ).toFile()) : null;
    }

    public static File downloadIfFirstLaunch(String url) throws IOException {
        return downloadIfFirstLaunch(Application.modJarsFolder, url);
    }

    public static File downloadIfFirstLaunch(String url, String hash) throws IOException, NoSuchAlgorithmException {
        File f = downloadIfFirstLaunch(Application.modJarsFolder, url);
        if(Objects.equals(hash, getSHA256Checksum(f)))
            return f;

        throw new RuntimeException("Hashes do not match! (file " + f.getAbsolutePath() + ")");
    }

    public static String getSHA256Checksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file);
             DigestInputStream dis = new DigestInputStream(fis, digest)) {
            byte[] buffer = new byte[1024];
            while (dis.read(buffer) != -1) {}
        }
        return byteArray2Hex(digest.digest());
    }

    private static String byteArray2Hex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hash = formatter.toString();
        formatter.close();
        return hash;
    }
}

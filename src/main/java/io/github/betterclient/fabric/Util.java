package io.github.betterclient.fabric;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Util {
    public static ArrayList<JarEntry> getEntries(JarFile file) {
        return new ArrayList<>(Lists.newArrayList(file.entries().asIterator()));
    }

    public static byte[] readAndClose(InputStream str) throws IOException {
        byte[] bytes = str.readAllBytes();
        str.close();
        return bytes;
    }

    public static File urlToFile(String url) throws IOException {
        return Files.write(
                File.createTempFile(
                        url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf(".")),
                        url.substring(url.lastIndexOf(".") + 1)
                ).toPath(),

                readAndClose(new URL(url).openConnection().getInputStream())
        ).toFile();
    }

    public static File urlToFile(String url, File f) throws IOException {
        return Files.write(
                f.toPath(),
                readAndClose(new URL(url).openConnection().getInputStream())
        ).toFile();
    }

    public static File checkHashOrDownload(String url) throws IOException {
        String hashUrl = url.substring(0, url.lastIndexOf(".")) + "-hash.txt";
        String hash = Files.readString(urlToFile(hashUrl).toPath());

        Files.createDirectories(new File(".modjars").toPath());
        File alreadyDownloaded = new File(".modjars/" + url.substring(url.lastIndexOf("/") + 1));
        if(!alreadyDownloaded.exists()) {
            //file doesn't exist, probably first launch

            alreadyDownloaded.createNewFile();
            return urlToFile(url, alreadyDownloaded);
        }
        else {
            String checkHash = generateSHA256(alreadyDownloaded);

            if(hash.equals(checkHash)) {
                //file exists and hash matches

                return alreadyDownloaded;
            } else {
                //File exists but hash doesn't match, redownload

                alreadyDownloaded.delete();
                alreadyDownloaded.createNewFile();

                return urlToFile(url, alreadyDownloaded);
            }
        }
    }

    public static String generateSHA256(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);
            DigestInputStream dis = new DigestInputStream(fis, md);

            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);

            byte[] hash = md.digest();

            StringBuilder hexHash = new StringBuilder();
            for (byte b : hash) {
                String hex = String.format("%02x", b);
                hexHash.append(hex);
            }

            dis.close();

            return hexHash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

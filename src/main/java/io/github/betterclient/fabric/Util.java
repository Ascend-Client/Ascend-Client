package io.github.betterclient.fabric;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
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
}

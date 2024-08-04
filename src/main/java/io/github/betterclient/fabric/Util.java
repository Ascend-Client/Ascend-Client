package io.github.betterclient.fabric;

import io.github.betterclient.client.Application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        byte[] bytes = str.readAllBytes();
        str.close();
        return bytes;
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
}

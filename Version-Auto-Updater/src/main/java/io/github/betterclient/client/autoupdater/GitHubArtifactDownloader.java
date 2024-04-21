package io.github.betterclient.client.autoupdater;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class GitHubArtifactDownloader {
    public static File download(File to) throws IOException {
        URL url = new URL("https://nightly.link/betterclient/Minecraft-Client/workflows/commit/modern/Versions.zip");
        InputStream is = url.openStream();
        byte[] bites = is.readAllBytes();
        is.close();
        Files.write(to.toPath(), bites);
        to.deleteOnExit();
        return to;
    }
}
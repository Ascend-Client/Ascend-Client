package io.github.betterclient.client.autoupdater;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class GitHubArtifactDownloader {
    public static File download(File to) throws IOException, URISyntaxException {
        URL url = new URI("https://nightly.link/Ascend-Client/Ascend-Client/workflows/commit/modern/Versions.zip").toURL();
        InputStream is = url.openStream();
        byte[] bites = is.readAllBytes();
        is.close();
        Files.write(to.toPath(), bites);
        to.deleteOnExit();
        return to;
    }
}
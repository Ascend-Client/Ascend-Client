package io.github.betterclient.client.autoupdater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipFile;

public class Main {
    public static AtomicReference<StatusFrame> statusFrameAtomicReference = new AtomicReference<>(null);

    public static void main(String[] args) throws IOException, URISyntaxException {
        new Thread(() -> statusFrameAtomicReference.set(new StatusFrame(args[0]))).start();
        while (statusFrameAtomicReference.get() == null) {/* wait */}
        String version = args[0];
        String filePath = new File("").getAbsolutePath();
        File toInstall = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)) + File.separator + "libraries" + File.separator + "customjar-1.jar");
        toInstall.delete();

        File versions = File.createTempFile("versions", ".zip");
        versions.deleteOnExit();
        ZipFile file = new ZipFile(GitHubArtifactDownloader.download(versions));
        InputStream is = file.getInputStream(file.getEntry(version + ".jar"));
        byte[] versionJar = is.readAllBytes();
        is.close();
        file.close();

        Files.write(toInstall.toPath(), versionJar);

        System.exit(0);
    }
}

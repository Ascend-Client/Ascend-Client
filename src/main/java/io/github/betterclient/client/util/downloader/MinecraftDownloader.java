package io.github.betterclient.client.util.downloader;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.Util;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class MinecraftDownloader {
    public static DownloadedMinecraft downloadMinecraft(MinecraftVersion version) throws Exception {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();

        bridge.info("Downloading and setting up minecraft version: " + version.version());

        File intermediaryFile = new File(Application.mcVersionFolder, "intermediary.jar");
        File yarnFile = new File(Application.mcVersionFolder, "yarn.jar");
        File intermediaryToYarn = new File(Application.mcVersionFolder, "mappings.tiny");
        if(yarnFile.exists() && intermediaryFile.exists() && intermediaryToYarn.exists() && !Application.ignoreDownloadedMinecraft) {
            bridge.info("Found pre-downloaded mc file, using it (delete " + Application.mcVersionFolder.getAbsolutePath() + " if issues occur)");
            return new DownloadedMinecraft(intermediaryFile, yarnFile, intermediaryToYarn, version);
        }
        yarnFile.delete();
        yarnFile.createNewFile();
        intermediaryFile.delete();
        intermediaryFile.createNewFile();
        intermediaryToYarn.delete();
        intermediaryToYarn.createNewFile();

        File clientJar = downloadFile(version.clientJar(), "jar");
        File intermediaryTiny = downloadFile(version.intermediaryTiny(), "tiny");
        File yarnTiny = downloadFile(version.yarnTiny(), "tiny");

        Files.copy(yarnTiny.toPath(), intermediaryToYarn.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Map<String, byte[]> toAdd = new HashMap<>();
        JarFile clientJarFile = new JarFile(clientJar);
        for (JarEntry entry : Util.getEntries(clientJarFile)) {
            byte[] bites = Util.readAndClose(clientJarFile.getInputStream(entry));
            if(!entry.getName().endsWith(".class") && !entry.getName().equals("META-INF/MANIFEST.MF") && !entry.isDirectory() && !entry.getName().equals("log4j.xml")) {
                if(bites != null)
                    toAdd.put(entry.getName(), bites);
            }
        }
        clientJarFile.close();

        map(clientJar, intermediaryTiny, intermediaryFile, "official", "intermediary", new HashMap<>());
        map(intermediaryFile, intermediaryToYarn, yarnFile, "intermediary", "named", toAdd);

        return new DownloadedMinecraft(intermediaryFile, yarnFile, intermediaryToYarn, version);
    }

    private static void map(File toMap, File tinyMappings, File destination, String from, String to, Map<String, byte[]> toAdd) throws IOException {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();
        bridge.info("Mapping: " + destination.getName() + " with: " + tinyMappings.getName());

        TinyRemapper.Builder builder = TinyRemapper.newRemapper();
        builder.withMappings(TinyUtils.createTinyMappingProvider(tinyMappings.toPath(), from, to));
        builder.fixPackageAccess(true);
        builder.resolveMissing(true);
        TinyRemapper mapper = builder.build();

        JarOutputStream jos = new JarOutputStream(Files.newOutputStream(destination.toPath()));

        Map<String, byte[]> files = new HashMap<>();
        mapper.readInputs(toMap.toPath());
        mapper.apply(files::put);

        for (String s : files.keySet()) {
            byte[] file = files.get(s);
            if(file == null) {
                bridge.info(s + " is null!");
                continue;
            }

            jos.putNextEntry(new JarEntry(s + ".class"));
            jos.write(file);
            jos.closeEntry();
        }

        for (Map.Entry<String, byte[]> entry : toAdd.entrySet()) {
            byte[] file = entry.getValue();
            if(file == null) {
                bridge.info(entry.getKey() + " is null!");
                continue;
            }

            jos.putNextEntry(new JarEntry(entry.getKey()));
            jos.write(file);
            jos.closeEntry();
        }

        jos.close();
        bridge.info("Mapped!");
    }

    private static File downloadFile(String url, String prefix) throws Exception {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();
        bridge.info("Downloading file: " + url);

        URL url1 = new URL(url);
        InputStream is = url1.openStream();
        byte[] downloaded = is.readAllBytes();
        is.close();
        File file = File.createTempFile("download", "." + prefix);
        file.deleteOnExit();
        Files.write(file.toPath(), downloaded);
        return file;
    }
}

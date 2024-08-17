package io.github.betterclient.client;

import io.github.betterclient.client.asm.YarnFix;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.DownloadedMinecraft;
import io.github.betterclient.client.util.downloader.MinecraftDownloader;
import io.github.betterclient.client.util.mclaunch.StatusFrame;
import io.github.betterclient.client.util.modremapper.utility.ModLoadingInformation;
import io.github.betterclient.client.util.modremapper.ModRemapper;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.quixotic.Quixotic;
import io.github.betterclient.quixotic.QuixoticClassLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Application {
    public static File
            clientFolder = new File(".ballsack"),
            configFolder = new File(clientFolder, "config"),
            modJarsFolder = new File(clientFolder, "modjars"),
            errorsFolder = new File(clientFolder, "error-reports"),
            mcDownloadsFolder = new File(clientFolder, "downloads"),
            customJarsFolder = new File(clientFolder, "custom-mods"),
            remappedModsFolder = new File(clientFolder, "remapped-mods"),
            remappedModJarsFolder = new File(modJarsFolder, "remapped"),
            remappedBuiltinModJarsFolder = new File(remappedModJarsFolder, "builtin"),
            customJarsRequestedFolder = new File(customJarsFolder, "requested"),
            mcVersionFolder;

    public static final boolean doRemappingOfAlreadyRemappedMods = false,
                          ignoreDownloadedMinecraft = false,
                          isDev = Boolean.getBoolean("fabric.development");
    public static DownloadedMinecraft minecraft;

    public static AtomicReference<StatusFrame> statusFrame = new AtomicReference<>(null);
    public static ModLoadingInformation modLoadingInformation = new ModLoadingInformation(new ArrayList<>(), new ArrayList<>(), ModLoadingInformation.State.LOADING_BUILTIN, null);

    public static void load(QuixoticClassLoader quixoticClassLoader) {
        quixoticClassLoader.addExclusion("io.github.betterclient.client.asm.Better");
        quixoticClassLoader.addExclusion("io.github.betterclient.client.asm.ASMHelper");
        quixoticClassLoader.addExclusion("org.slf4j.");

        try {
            /*for (File file : remappedBuiltinModJarsFolder.listFiles()) {
                file.delete();
            }

            for (File file : remappedModJarsFolder.listFiles()) {
                file.delete();
            }*/
            Files.createDirectories(clientFolder.toPath());
            Files.createDirectories(modJarsFolder.toPath());
            Files.createDirectories(configFolder.toPath());
            Files.createDirectories(errorsFolder.toPath());
            Files.createDirectories(mcDownloadsFolder.toPath());
            Files.createDirectories(customJarsFolder.toPath());
            Files.createDirectories(remappedModsFolder.toPath());
            Files.createDirectories(remappedModJarsFolder.toPath());
            Files.createDirectories(remappedBuiltinModJarsFolder.toPath());
            Files.createDirectories(customJarsRequestedFolder.toPath());
            Files.createDirectories(mcVersionFolder.toPath());
        } catch (Exception e) { IBridge.getPreLaunch().error(e); }

        boolean hasDownloaded = false;
        try {
            //download mc
            minecraft = MinecraftDownloader.downloadMinecraft(IBridge.getPreLaunch().getVersion());
            hasDownloaded = true;

            //remove mc if it exists
            URL mcURL = null;
            for (Object url : Quixotic.classLoader.getURLs()) {
                if(url instanceof URL listURL && listURL.toString().contains("minecraft"))
                    mcURL = listURL;
            }

            IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();

            if(mcURL != null) {
                Field f = URLClassLoader.class.getDeclaredField("ucp");
                f.setAccessible(true);
                Object ucp = f.get(quixoticClassLoader);

                f = ucp.getClass().getDeclaredField("path");
                f.setAccessible(true);
                ArrayList<?> path = (ArrayList<?>) f.get(ucp);

                f = ucp.getClass().getDeclaredField("unopenedUrls");
                f.setAccessible(true);
                ArrayDeque<?> unopenedUrls = (ArrayDeque<?>) f.get(ucp);

                path.remove(mcURL);
                unopenedUrls.remove(mcURL);
                bridge.info("Removed Minecraft");
            }

            Quixotic.classLoader.addURL((isDev ? minecraft.yarnJar() : minecraft.intermediaryJar()).toURI().toURL());
            bridge.info("Re-added minecraft");
        } catch (Exception e) {
            throw new RuntimeException("Failed to " + (hasDownloaded ? "replace" : "download") +  " minecraft.", e);
        }

        long startt = System.currentTimeMillis();

        try {
            new Thread(StatusFrame::new).start();
            while (statusFrame.get() == null) {}
            modLoadingInformation = new ModLoadingInformation(new ArrayList<>(), new ArrayList<>(), ModLoadingInformation.State.LOADING_BUILTIN, null);

            JarFile f = new JarFile(minecraft.yarnJar());

            for (JarEntry entry : Util.getEntries(f)) {
                if(entry.getName().endsWith(".class")) modLoadingInformation.minecraftClasses().add(entry.getName());
            }

            f.close();
            modLoadingInformation.nonCustomMods().addAll(IBridge.getPreLaunch().getVersionMods());
            for (File file : modLoadingInformation.nonCustomMods()) {
                FabricLoader.getInstance().loadMod(
                        ModRemapper.remapInternalMod(
                                file, false
                        )
                );
            }

            modLoadingInformation = new ModLoadingInformation(modLoadingInformation.minecraftClasses(), modLoadingInformation.nonCustomMods(), ModLoadingInformation.State.LOADING_CUSTOM, null);

            for (File customMod : Objects.requireNonNull(customJarsFolder.listFiles())) {
                if(customMod.getName().endsWith(".jar")) {
                    long start = System.currentTimeMillis();
                    FabricMod loaded = FabricLoader.getInstance().loadMod(ModRemapper.remapMod(customMod, false));
                    long time = System.currentTimeMillis() - start;
                    if(loaded != null)
                        IBridge.getPreLaunch().info("Mod " + loaded.name() + " loaded in " + time / 1000 + " seconds");
                }
            }

            modLoadingInformation = new ModLoadingInformation(modLoadingInformation.minecraftClasses(), modLoadingInformation.nonCustomMods(), ModLoadingInformation.State.LOADED_ALL, null);
            statusFrame.get().setVisible(false);
            statusFrame.get().dispose();
        } catch (Exception e) {
            throw new RuntimeException("Mod loading failed while " + modLoadingInformation.state().getName(), e);
        }

        IBridge.getPreLaunch().info("Took " + (System.currentTimeMillis() - startt) / 1000f + " seconds for remapping of all mods!");

        quixoticClassLoader.addPlainTransformer(new YarnFix());

        FabricLoader.getInstance().loadApplicationManager(quixoticClassLoader);
    }
}
package io.github.betterclient.client.util.modremapper;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.client.util.modremapper.utility.ModIssueFixer;
import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.Util;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import static io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility.convertLangToJSON;
import static io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility.generateFabricLoaderMappings;

public class ProdFabricRemapper {
    public static File remap(File modToRemap, boolean isBuiltin, boolean isInternal) throws IOException {
        IBridge.PreLaunchBridge bridge = IBridge.getPreLaunch();
        File remappedMod;

        if(isInternal) {
            remappedMod = new File(Application.remappedModJarsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remappedF.jar");
            if(isBuiltin) {
                remappedMod = new File(Application.remappedBuiltinModJarsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remappedF.jar");
            }
        } else {
            remappedMod = new File(Application.remappedModsFolder, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remappedF.jar");
            if(isBuiltin) {
                remappedMod = new File(Application.remappedModsFolder, "builtin");
                Files.createDirectories(remappedMod.toPath());
                remappedMod = new File(remappedMod, modToRemap.getName().substring(0, modToRemap.getName().lastIndexOf('.')) + "-remappedF.jar");
            }
        }

        if(remappedMod.exists() && !Application.doRemappingOfAlreadyRemappedMods) {
            if(Util.readAndClose(new FileInputStream(remappedMod)).length != 0)
                return remappedMod;
            else
                bridge.info("Found corrupted mod file, deleting and remapping");
        }

        String modName = FabricLoader.getInstance().getModName(modToRemap);
        bridge.info("Remapping fabric mod " + modName);

        if(modName.startsWith("kotlin-") || modName.startsWith("kotlinx-"))
            return modToRemap;

        remappedMod.delete();
        if(!remappedMod.createNewFile()) {
            bridge.error("Failed to create file (?)");
        }

        Map<String, byte[]> finalFile = mapFabric(modToRemap);

        JarFile file = new JarFile(modToRemap);
        for (JarEntry entry : Util.getEntries(file)) {
            if(!entry.getName().endsWith(".class") && !entry.getName().equals("META-INF/MANIFEST.MF")) {
                byte[] bites = Util.readAndClose(file.getInputStream(entry));
                if(entry.getName().endsWith(".json")) {
                    String str = ModRemapper.fixIssue(new String(bites));
                    str = String.join("\n", Arrays.stream(str.split("\n")).filter(string -> !string.replaceAll(" ", "").startsWith("//")).toArray(String[]::new));
                    str = str.replace("\"MixinMinecraft_NoAuthInDev\",", "");

                    if(Application.minecraft.version().version().equals(MinecraftVersion.Version.COMBAT_TEST_8C) && str.contains("method_3129(Z)V")) {
                        str = str.replace("method_3129(Z)V", "method_3129(ZZ)V");
                    }

                    bites = str.getBytes();
                }

                if(entry.getName().equals("fabric-screen-api-v1.mixins.json") && modName.equals("Fabric Screen API (v1)") && (Application.minecraft.version().version() == MinecraftVersion.Version.COMBAT_TEST_8C || Application.minecraft.version().version() == MinecraftVersion.Version.V1_19_4)) {
                    bites = new String(bites).replace("\"GameRendererMixin\",", "").getBytes();
                }

                finalFile.put(entry.getName(), bites);
            }
        }
        file.close();

        for (String s : new ArrayList<>(finalFile.keySet())) {
            if(s.endsWith(".class")) {
                ClassReader reader = new ClassReader(finalFile.get(s));
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                try {
                    ModIssueFixer.edit(node, modToRemap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                finalFile.put(s, writer.toByteArray());
            } else if(s.endsWith(".lang")) {
                String oldS = s;
                s = s.substring(0, s.lastIndexOf('.')) + ".json";

                finalFile.put(s, convertLangToJSON(finalFile.get(oldS)));
            }
        }

        JarOutputStream jos = new JarOutputStream(Files.newOutputStream(remappedMod.toPath()));

        for (String s : finalFile.keySet()) {
            byte[] bytes = finalFile.get(s);

            jos.putNextEntry(new JarEntry(s));
            jos.write(bytes);
            jos.closeEntry();
        }

        jos.close();

        return remappedMod;
    }

    private static Map<String, byte[]> mapFabric(File modToRemap) throws IOException {
        TinyRemapper.Builder builder = TinyRemapper.newRemapper();
        builder.withMappings(TinyUtils.createTinyMappingProvider(generateFabricLoaderMappings().toPath(), "fabric", "ballsack"));
        builder.ignoreConflicts(true);
        builder.threads(1);
        TinyRemapper remapper = builder.build();

        int classSize = 0;
        {
            try {
                JarFile files = new JarFile(modToRemap);
                for (JarEntry entry : Util.getEntries(files)) {
                    if(entry.getName().endsWith(".class"))
                        classSize++;
                }
                files.close();
            } catch (IOException e) {
                classSize = 100000;
            }
        }

        Map<String, byte[]> finalFile = new HashMap<>();

        Map<String, byte[]> mappings = new HashMap<>();
        List<Path> files = new ArrayList<>();
        files.add(modToRemap.toPath());
        remapper.readInputs(files.toArray(Path[]::new));
        remapper.apply((s, bytes) -> {
            synchronized (new Object()) {
                mappings.put(s, bytes);
            }
        });

        if(classSize != 100000) {
            while((mappings.size() + 1) != classSize && mappings.size() != classSize) {} //Wait For remapper to finish
            remapper.finish();
        }

        for (String s : mappings.keySet()) {
            byte[] bytes = mappings.get(s);
            if(bytes == null) continue;
            if(Application.modLoadingInformation.minecraftClasses().contains(s + ".class")) continue;

            finalFile.put(s + ".class", bytes);
        }

        return finalFile;
    }
}

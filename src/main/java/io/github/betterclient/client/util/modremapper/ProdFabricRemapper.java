package io.github.betterclient.client.util.modremapper;

import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import io.github.betterclient.fabric.Util;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ProdFabricRemapper {
    public static Map<String, byte[]> FABRIC_MAPPING_DATA = new HashMap<>();

    public static File remap(File modToRemap) throws IOException {
        TinyRemapper.Builder builder = TinyRemapper.newRemapper();
        builder.withMappings(TinyUtils.createTinyMappingProvider(ModRemapperUtility.generateFabricLoaderMappings().toPath(), "fabric", "ascend"));
        builder.ignoreConflicts(true);
        builder.threads(1);
        TinyRemapper remapper = builder.build();

        int classSize = 0;
        {
            try(JarFile files = new JarFile(modToRemap)) {
                for (JarEntry entry : Util.getEntries(files)) {
                    if(entry.getName().endsWith(".class"))
                        classSize++;
                }
            } catch (IOException e) {
                classSize = 100000;
            }
        }

        Map<String, byte[]> mappings = new HashMap<>();
        remapper.readInputs(modToRemap.toPath());
        remapper.apply((s, bytes) -> {
            synchronized (new Object()) {
                mappings.put(s, bytes);
            }
        });

        if(classSize != 100000) {
            while((mappings.size() + 1) != classSize && mappings.size() != classSize) {} //Wait For remapper to finish
        }
        remapper.finish();

        for (String s : mappings.keySet()) {
            byte[] bytes = mappings.get(s);
            if(bytes == null) continue;

            FABRIC_MAPPING_DATA.put(s, bytes);
        }

        return modToRemap;
    }
}
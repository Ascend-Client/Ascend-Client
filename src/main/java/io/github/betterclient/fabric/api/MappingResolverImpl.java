package io.github.betterclient.fabric.api;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.DownloadedMinecraft;
import io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.fabric.relocate.loader.api.MappingResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MappingResolverImpl implements MappingResolver {
    public static MappingResolver instance = new MappingResolverImpl(Application.minecraft);
    private final Map<String, String> intermediaryToYarn = new HashMap<>();
    private final Map<String, String> officialMappings = new HashMap<>();

    public MappingResolverImpl(DownloadedMinecraft minecraft) {
        try {
            this.intermediaryToYarn.putAll(ModRemapperUtility.generateMappings(minecraft));
            officialMappings.putAll(populateOfficialMappings(minecraft));
        } catch (IOException e) {
            IBridge.getPreLaunch().error("Failed to load mappings!");
            IBridge.getPreLaunch().error(e);
        }
    }

    private Map<String, String> populateOfficialMappings(DownloadedMinecraft minecraft) throws IOException {
        Map<String, String> out = new HashMap<>();
        File officialMaps = Util.downloadIfFirstLaunch(Application.mcVersionFolder, minecraft.version().clientTxt());
        File tinyMaps = Util.downloadIfFirstLaunch(Application.mcVersionFolder, minecraft.version().intermediaryTiny());
        if(officialMaps == null) return out;
        if(tinyMaps == null) return out;

        for (String s : Files.readString(officialMaps.toPath()).split("\n")) {
            if(s.startsWith(" ") || s.startsWith("#") || s.endsWith(".") || s.isEmpty() || s.isBlank()) continue;

            s = s.substring(0, s.length() - 1);
            String[] mapping = s.split(" -> ");
            out.put(mapping[0], mapping[1]);
        }

        for (String s : Files.readString(tinyMaps.toPath()).split("\n")) {
            if(s.startsWith("CLASS\t")) {
                String[] mapping = s.split("\t");

                if(out.containsValue(mapping[1])) {
                    out.put(out.keySet().stream().toList().get(out.values().stream().toList().indexOf(mapping[1])), mapping[2]);
                }
            }
        }

        if(Application.isDev) {
            for (String s : new ArrayList<>(out.keySet())) {
                out.put(s, this.intermediaryToYarn.getOrDefault(out.get(s), out.get(s)));
            }
        }


        return out;
    }

    @Override
    public String mapClassName(String namespace, String className) {
        switch (namespace) {
            case "named" -> {
                return Application.isDev ? className : intermediaryToYarn.keySet().stream().toList().get(new ArrayList<>(intermediaryToYarn.values()).indexOf(className));
            }
            case "official" -> {
                return officialMappings.getOrDefault(className, className);
            }
            default -> {
                return Application.isDev ? ModRemapperUtility.mapClassName(className.replace('.', '/'), this.intermediaryToYarn).replace('/', '.') : className;
            }
        }
    }
}

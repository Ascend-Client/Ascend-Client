package io.github.betterclient.fabric.api;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.relocate.loader.api.FabricLoader;
import io.github.betterclient.fabric.relocate.loader.api.MappingResolver;
import io.github.betterclient.fabric.relocate.loader.api.ModContainer;
import io.github.betterclient.fabric.relocate.loader.api.ObjectShare;
import io.github.betterclient.fabric.relocate.loader.api.entrypoint.EntrypointContainer;
import io.github.betterclient.quixotic.Quixotic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class FabricLoaderImpl implements FabricLoader {
    private Path configDir;

    @Override
    public Path getConfigDir() {
        if(configDir == null) {
            configDir = new File("./fabricConfig").toPath();
        }

        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            throw new RuntimeException("Creating config directory", e);
        }

        return configDir;
    }

    @Override
    public <T> List<EntrypointContainer<T>> getEntrypointContainers(String key, Class<T> type) {
        ArrayList<EntrypointContainer<T>> ts = new ArrayList<>();
        for (FabricMod loadedMod : io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods) {
            for (String keya : loadedMod.allEntries().keySet()) {
                String val = loadedMod.allEntries().get(keya);
                if(keya.equals(key)) {
                    try {
                        Class<?> outClass = Class.forName(val);
                        Object instance = outClass.getConstructor().newInstance();

                        ts.add(new EntrypointContainerImpl<>(type.cast(instance), this.getModContainer(loadedMod.name())));
                    } catch (Exception e) {
                        System.out.println(loadedMod.name());
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return ts;
    }

    @Override
    public File getConfigDirectory() {
        if(configDir == null) {
            configDir = new File("./fabricConfig").toPath();
        }

        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            throw new RuntimeException("Creating config directory", e);
        }

        return configDir.toFile();
    }

    @Override
    public MappingResolver getMappingResolver() {
        return MappingResolverImpl.instance;
    }

    @Override
    public boolean isModLoaded(String name) {
        return io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods.stream().anyMatch(fabricMod -> fabricMod.name().equalsIgnoreCase(name) || fabricMod.id().equalsIgnoreCase(name));
    }

    @Override
    public Optional<ModContainer> getModContainer(String name) {
        Optional<FabricMod> mod = io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods.stream().filter(fabricMod -> fabricMod.name().equalsIgnoreCase(name) || fabricMod.id().equalsIgnoreCase(name)).findFirst();
        ModContainer container;
        container = mod.map(ModContainerImpl::new).orElse(null);

        return Optional.ofNullable(container);
    }

    @Override
    public Collection<ModContainer> getAllMods() {
        List<ModContainer> mods = new Vector<>();

        io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods.forEach(fabricMod -> mods.add(new ModContainerImpl(fabricMod)));

        return mods;
    }

    @Override
    public <T> List<T> getEntrypoints(String key, Class<T> type) {
        ArrayList<T> ts = new ArrayList<>();
        for (FabricMod loadedMod : io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods) {
            for (String keya : loadedMod.allEntries().keySet()) {
                String val = loadedMod.allEntries().get(keya);
                if(keya.equals(key)) {
                    if(type.equals(Consumer.class)) {
                        Consumer<?> a = o -> {
                            try {
                                Class<?> lmd = Class.forName(val.substring(0, val.indexOf(":")), false, Quixotic.classLoader);
                                String methodName = val.substring(val.lastIndexOf(":") + 1);
                                Method mde = lmd.getDeclaredMethod(methodName, Function.class);
                                if(Modifier.isStatic(mde.getModifiers())) {
                                    mde.invoke(null, o);
                                } else {
                                    mde.invoke(lmd.getConstructor().newInstance(), o);
                                }
                            } catch (Exception e) {
                                IBridge.getPreLaunch().error(e.toString());
                            }
                        };

                        ts.add((T) a);
                    }
                }
            }
        }

        return ts;
    }

    ObjectShareImpl impl = new ObjectShareImpl();

    @Override
    public ObjectShare getObjectShare() {
        return impl;
    }
}

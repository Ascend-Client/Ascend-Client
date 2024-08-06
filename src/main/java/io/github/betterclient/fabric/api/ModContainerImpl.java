package io.github.betterclient.fabric.api;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.relocate.loader.api.*;
import io.github.betterclient.fabric.relocate.loader.api.metadata.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ModContainerImpl implements ModContainer {
    public final FabricMod mod;

    public ModContainerImpl(FabricMod mod) {
        this.mod = mod;
    }

    @Override
    public ModMetadata getMetadata() {
        return new ModMetadata() {
            @Override
            public String getName() {
                return ModContainerImpl.this.mod.name();
            }

            @Override
            public String getType() {
                return "iforgot";
            }

            @Override
            public Version getVersion() {
                try {
                    return SemanticVersion.parse(ModContainerImpl.this.mod.version());
                } catch (VersionParsingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean containsCustomValue(String s) {
                return mod.customValues().containsKey(s);
            }

            @Override
            public CustomValue getCustomValue(String s) {
                return mod.customValues().get(s);
            }

            @Override
            public ModEnvironment getEnvironment() {
                return mod.environment();
            }

            @Override
            public Collection<String> getProvides() {
                return List.of();
            }

            @Override
            public String getDescription() {
                return mod.description();
            }

            @Override
            public ContactInformation getContact() {
                return mod.getContact();
            }

            @Override
            public Collection<Person> getAuthors() {
                return mod.authors();
            }

            @Override
            public Collection<Person> getContributors() {
                return mod.contributors();
            }

            @Override
            public Optional<String> getIconPath(int size) {
                return mod.getIconMap() == null ? Optional.empty() : mod.getIconMap().get(size);
            }

            @Override
            public String getId() {
                return ModContainerImpl.this.mod.id();
            }
        };
    }

    @Override
    public ModOrigin getOrigin() {
        return new ModOrigin() {
            @Override
            public Kind getKind() {
                return mod.getContainer() == null ? Kind.PATH : Kind.NESTED;
            }

            @Override
            public List<Path> getPaths() {
                return List.of(mod.from().toPath());
            }
        };
    }

    @Override
    public Optional<ModContainer> getContainingMod() {
        if(this.mod.getContainer() == null) {
            return Optional.empty();
        }

        return FabricLoader.getInstance().getModContainer(this.mod.getContainer());
    }

    @Override
    public Collection<ModContainer> getContainedMods() {
        List<ModContainer> mods = new ArrayList<>();
        for (FabricMod loadedMod : io.github.betterclient.fabric.FabricLoader.getInstance().loadedMods) {
            if(this.mod.name().equals(loadedMod.getContainer())) {
               mods.add(new ModContainerImpl(loadedMod));
            }
        }

        return mods;
    }

    @Override
    public List<Path> getRootPaths() {
        return List.of(this.mod.from().toPath());
    }

    @Override
    public Path getRootPath() {
        return this.mod.from().toPath();
    }

    @Override
    public Path getPath(String s) {
        URI jarUri = URI.create("jar:file:" + this.mod.from().toURI().getPath());

        try (FileSystem jarFileSystem = FileSystems.newFileSystem(jarUri, Collections.emptyMap())) {
            Path fileInsideJarPath = jarFileSystem.getPath(s);

            File theRequestedFile = new File(Application.customJarsRequestedFolder, this.mod.from().getName() + "-" + s.substring(s.lastIndexOf("/") + 1));
            if(theRequestedFile.exists())
                return theRequestedFile.toPath();

            byte[] fileContent = Files.readAllBytes(fileInsideJarPath);
            Files.write(theRequestedFile.toPath(), fileContent);
            return theRequestedFile.toPath();
        } catch (IOException e) {
            IBridge.getPreLaunch().error(e);
        }

        return null;
    }
}

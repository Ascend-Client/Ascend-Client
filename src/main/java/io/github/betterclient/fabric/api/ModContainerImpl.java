package io.github.betterclient.fabric.api;

import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.relocate.loader.api.*;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ModMetadata;
import io.github.betterclient.fabric.relocate.loader.api.metadata.ModOrigin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
}

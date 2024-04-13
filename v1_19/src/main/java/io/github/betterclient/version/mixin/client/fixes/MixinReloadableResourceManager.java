package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.*;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static io.github.betterclient.fabric.Util.readAndClose;

@Mixin(ResourceFactory.class)
public interface MixinReloadableResourceManager {
    @Shadow
    Optional<Resource> getResource(Identifier id);

    /**
     * @author betterclient
     * @reason idk
     */
    @Overwrite
    default Resource getResourceOrThrow(Identifier id) throws FileNotFoundException {
        Optional<Resource> of = this.getResource(id);
        if(of.isEmpty()) {
            return findLoadedResource(id);
        } else {
            return of.orElseThrow(FileNotFoundException::new);
        }
    }

    default Resource findLoadedResource(Identifier id) {
        String addr = "assets/" + id.getNamespace() + "/" + id.getPath();

        for (FabricMod mod : FabricLoader.getInstance().loadedMods) {
            try {
                JarFile f = new JarFile(mod.from());
                ZipEntry entry = f.getEntry(addr);
                if(entry != null) {
                    InputStream is = f.getInputStream(entry);
                    return new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), () -> new ByteArrayInputStream(readAndClose(is)));
                }
                f.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}

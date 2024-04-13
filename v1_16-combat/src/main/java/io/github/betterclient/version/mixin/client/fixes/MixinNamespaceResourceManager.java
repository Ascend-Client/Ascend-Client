package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.Util;
import io.github.betterclient.version.util.ResourceImplementation;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

@Mixin(NamespaceResourceManager.class)
public class MixinNamespaceResourceManager {
    @Inject(method = "getResource", at = @At(value = "NEW", args = "class=java/io/FileNotFoundException"), cancellable = true)
    public void hi(Identifier id, CallbackInfoReturnable<Resource> cir) {
        Resource resource = findLoadedResource(id);

        if(resource != null) {
            cir.setReturnValue(resource);
            cir.cancel();
        }
    }

    private Resource findLoadedResource(Identifier id) {
        String addr = "assets/" + id.getNamespace() + "/" + id.getPath();

        for (FabricMod mod : FabricLoader.getInstance().loadedMods) {
            try {
                JarFile f = new JarFile(mod.from());
                ZipEntry entry = f.getEntry(addr);
                if(entry != null) {
                    InputStream is = f.getInputStream(entry);
                    return new ResourceImplementation(() -> {
                        try {
                            return new ByteArrayInputStream(Util.readAndClose(is));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                f.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }
}

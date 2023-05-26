package net.fabricmc.fabric.api.mixins;

import io.github.betterclient.fabric.FabricLoader;
import io.github.betterclient.fabric.FabricMod;
import io.github.betterclient.fabric.Util;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.Resource;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mixin(TranslationStorage.class)
public abstract class MixinTranslationStorage {
    @Inject(method = "load(Ljava/util/List;Ljava/util/Map;)V", at = @At("HEAD"))
    private static void createModFiles(List<Resource> resources, Map<String, String> translationMap, CallbackInfo ci) {
        try {
            FabricLoader.getInstance().callClientMain();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            for (FabricMod mod : FabricLoader.getInstance().loadedMods) {
                JarFile f = new JarFile(mod.from());

                for (JarEntry entry : Util.getEntries(f)) {
                    if(entry.getName().contains("en_us.json")) {
                        JSONObject obj = new JSONObject(new String(Util.readAndClose(f.getInputStream(entry))));

                        for (String key : obj.keySet()) {
                            translationMap.put(key, obj.getString(key));
                        }
                    }
                }

                f.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        translationMap.put("gui.socialInteractions.tab_all", "All");
    }
}
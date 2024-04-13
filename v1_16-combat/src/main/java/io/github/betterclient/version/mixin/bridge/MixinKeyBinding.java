package io.github.betterclient.version.mixin.bridge;

import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Shadow @Final private static Map<String, Integer> categoryOrderMap;

    @Inject(method = "<init>(Ljava/lang/String;ILjava/lang/String;)V", at = @At("RETURN"))
    public void addCategoryIfNotExist(String translationKey, int code, String category, CallbackInfo ci) {
        if(!categoryOrderMap.containsKey(category)) {
            int max = 0;
            for (int value : categoryOrderMap.values()) {
                if (value > max) {
                    max = value;
                }
            }

            categoryOrderMap.put(category, max + 1);
        }
    }
}

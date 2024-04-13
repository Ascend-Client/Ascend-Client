package io.github.betterclient.version.mixin.bridge;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
    @Shadow @Final private static Map<String, Integer> CATEGORY_ORDER_MAP;

    @Inject(method = "<init>(Ljava/lang/String;ILjava/lang/String;)V", at = @At("RETURN"))
    public void addCategoryIfNotExist(String translationKey, int code, String category, CallbackInfo ci) {
        if(!CATEGORY_ORDER_MAP.containsKey(category)) {
            int max = 0;
            for (int value : CATEGORY_ORDER_MAP.values()) {
                if (value > max) {
                    max = value;
                }
            }

            CATEGORY_ORDER_MAP.put(category, max + 1);
        }
    }
}

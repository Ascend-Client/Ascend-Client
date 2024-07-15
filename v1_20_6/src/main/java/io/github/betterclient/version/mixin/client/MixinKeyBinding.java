package io.github.betterclient.version.mixin.client;

import io.github.betterclient.version.access.KeyBindingAccess;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements KeyBindingAccess {
    @Shadow private InputUtil.Key boundKey;

    @Override
    public int getKey() {
        return this.boundKey.getCode();
    }
}

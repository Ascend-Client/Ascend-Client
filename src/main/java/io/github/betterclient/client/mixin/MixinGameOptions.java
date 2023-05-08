package io.github.betterclient.client.mixin;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinGameOptions {
    @Mutable
    @Shadow
    @Final
    public KeyBinding[] keysAll;
}

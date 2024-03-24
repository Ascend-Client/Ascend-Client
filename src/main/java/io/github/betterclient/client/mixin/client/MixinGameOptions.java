package io.github.betterclient.client.mixin.client;

import io.github.betterclient.client.access.GameOptionsAccess;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinGameOptions implements GameOptionsAccess {
    @Mutable
    @Shadow
    @Final
    public KeyBinding[] allKeys;

    @Shadow private Perspective perspective;

    @Override
    public void setPerspective(Perspective p) {
        this.perspective = p;
    }
}

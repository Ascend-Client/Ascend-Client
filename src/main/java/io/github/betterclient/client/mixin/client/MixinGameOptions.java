package io.github.betterclient.client.mixin.client;

import io.github.betterclient.client.access.GameOptionsAccess;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Perspective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
public class MixinGameOptions implements GameOptionsAccess {
    @Mutable
    @Shadow
    @Final
    public KeyBinding[] keysAll;

    @Shadow private Perspective field_26677;

    @Override
    public void setPerspective(Perspective p) {
        this.field_26677 = p;
    }
}

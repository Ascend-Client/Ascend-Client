package io.github.betterclient.client.mixin.client.fixes;

import net.minecraft.client.sound.SoundSystem;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public class MixinSoundManager {
    @Redirect(method = "reloadSounds", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public void ignoreLoggerCall(Logger instance, String s, Object o) { }
}

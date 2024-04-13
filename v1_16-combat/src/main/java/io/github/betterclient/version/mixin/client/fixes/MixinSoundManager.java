package io.github.betterclient.version.mixin.client.fixes;

import net.minecraft.client.sound.SoundSystem;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public class MixinSoundManager {
    @Redirect(method = "reloadSounds", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public void ignoreLoggerCall(Logger l, String s, Object o) { }
}

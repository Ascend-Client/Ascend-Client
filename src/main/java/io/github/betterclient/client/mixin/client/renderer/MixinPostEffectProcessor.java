package io.github.betterclient.client.mixin.client.renderer;

import io.github.betterclient.client.access.PostEffectProcessorAccessor;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PostEffectProcessor.class)
public class MixinPostEffectProcessor implements PostEffectProcessorAccessor {
    @Shadow @Final private List<PostEffectPass> passes;

    @Override
    public List<PostEffectPass> getPasses() {
        return this.passes;
    }
}

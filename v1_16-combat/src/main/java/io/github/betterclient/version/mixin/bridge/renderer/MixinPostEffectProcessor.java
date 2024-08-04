package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ShaderEffect.class)
public abstract class MixinPostEffectProcessor implements IBridge.ShaderEffect {
    @Shadow @Final private List<PostProcessShader> passes;

    @Override
    public List<IBridge.ShaderPass> getPasses() {
        return this.passes.stream().map(IBridge.ShaderPass.class::cast).toList();
    }
}

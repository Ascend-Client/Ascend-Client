package io.github.betterclient.client.mixin;

import io.github.betterclient.client.access.ShaderEffectAccessor;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ShaderEffect.class)
public class MixinShaderEffect implements ShaderEffectAccessor {
    @Shadow @Final private List<PostProcessShader> passes;

    @Override
    public List<PostProcessShader> getPasses() {
        return this.passes;
    }
}

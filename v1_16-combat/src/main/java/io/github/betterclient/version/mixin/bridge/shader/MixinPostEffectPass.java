package io.github.betterclient.version.mixin.bridge.shader;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.client.gl.PostProcessShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PostProcessShader.class)
public abstract class MixinPostEffectPass implements IBridge.ShaderPass {
    @Shadow public abstract JsonGlProgram getProgram();

    @Override
    public void setUniformByName(String uniformName, float num) {
        GlUniform shaderProgram = this.getProgram().getUniformByName(uniformName);
        if(shaderProgram != null)
            shaderProgram.set(num);
    }
}

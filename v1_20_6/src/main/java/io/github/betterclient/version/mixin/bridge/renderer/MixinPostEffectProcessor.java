package io.github.betterclient.version.mixin.bridge.renderer;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(PostEffectProcessor.class)
public class MixinPostEffectProcessor implements IBridge.ShaderEffect {
    @Shadow @Final private List<PostEffectPass> passes;

    @Shadow
    public void setupDimensions(int width, int height) {

    }

    @Override
    public List<IBridge.ShaderPass> getPasses() {
        List<IBridge.ShaderPass> returne = new ArrayList<>();
        for (PostEffectPass pass : this.passes) {
            returne.add((IBridge.ShaderPass) pass);
        }
        return returne;
    }

    @Shadow
    public void close() {

    }
}

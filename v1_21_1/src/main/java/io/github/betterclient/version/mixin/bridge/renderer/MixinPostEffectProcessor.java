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
public abstract class MixinPostEffectProcessor implements IBridge.ShaderEffect {
    @Shadow public abstract void close();

    @Shadow public abstract void setupDimensions(int targetsWidth, int targetsHeight);

    @Shadow @Final private List<PostEffectPass> passes;

    public void bs$setupDimensions(int width, int height) {setupDimensions(width, height);}

    @Override
    public List<IBridge.ShaderPass> getPasses() {
        List<IBridge.ShaderPass> returne = new ArrayList<>();
        for (PostEffectPass pass : this.passes) {
            returne.add((IBridge.ShaderPass) pass);
        }
        return returne;
    }

    public void bs$close() {close();}
}

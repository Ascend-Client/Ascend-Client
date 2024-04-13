package io.github.betterclient.version.access;

import net.minecraft.client.gl.PostEffectPass;

import java.util.List;

public interface PostEffectProcessorAccessor {
    List<PostEffectPass> getPasses();
}
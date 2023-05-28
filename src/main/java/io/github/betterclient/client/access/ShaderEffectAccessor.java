package io.github.betterclient.client.access;

import net.minecraft.client.gl.PostProcessShader;

import java.util.List;

public interface ShaderEffectAccessor {
    List<PostProcessShader> getPasses();
}
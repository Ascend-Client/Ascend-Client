package io.github.betterclient.version.mixin.bridge.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements IBridge.BufferBuilder {
    @Shadow public abstract void begin(int par1, VertexFormat par2);

    @Shadow public abstract void end();

    @Override
    public void begin(IBridge.BeginMode mode) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        int foundMode;

        switch (mode) {
            case LINES -> foundMode = GL11.GL_LINES;
            case LINE_STRIP -> foundMode = GL11.GL_LINE_STRIP;

            case TRIANGLES -> foundMode = GL11.GL_TRIANGLES;
            case TRIANGLE_FAN -> foundMode = GL11.GL_TRIANGLE_FAN;
            case TRIANGLE_STRIP -> foundMode = GL11.GL_TRIANGLE_STRIP;

            default -> foundMode = GL11.GL_QUADS;
        }

        this.begin(foundMode, VertexFormats.POSITION_COLOR);
    }

    @Override
    public void vertex(IBridge.MatrixStack matrices, float x, float y, float z, int color) {
        float f = (float)(color >> 24 & 255) / 255.0F;
        float g = (float)(color >> 16 & 255) / 255.0F;
        float h = (float)(color >> 8 & 255) / 255.0F;
        float j = (float)(color & 255) / 255.0F;
        ((BufferBuilder) (Object) this).vertex(((MatrixStack) matrices).peek().getModel(), x, y, z).color(g, h, j, f).next();
    }

    @Override
    public void draw() {
        this.end();
        BufferRenderer.draw((BufferBuilder) (Object) this);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}

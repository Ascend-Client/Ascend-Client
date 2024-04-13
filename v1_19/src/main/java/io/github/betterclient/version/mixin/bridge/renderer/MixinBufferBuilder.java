package io.github.betterclient.version.mixin.bridge.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements IBridge.BufferBuilder {
    @Shadow public abstract BufferBuilder.BuiltBuffer end();

    @Shadow public abstract void begin(VertexFormat.DrawMode drawMode, VertexFormat format);

    @Override
    public void begin(IBridge.BeginMode mode) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        VertexFormat.DrawMode foundMode;

        switch (mode) {
            case LINES -> foundMode = VertexFormat.DrawMode.LINES;
            case LINE_STRIP -> foundMode = VertexFormat.DrawMode.LINE_STRIP;
            case DEBUG_LINES -> foundMode = VertexFormat.DrawMode.DEBUG_LINES;
            case DEBUG_LINE_STRIP -> foundMode = VertexFormat.DrawMode.DEBUG_LINE_STRIP;

            case TRIANGLES -> foundMode = VertexFormat.DrawMode.TRIANGLES;
            case TRIANGLE_FAN -> foundMode = VertexFormat.DrawMode.TRIANGLE_FAN;
            case TRIANGLE_STRIP -> foundMode = VertexFormat.DrawMode.TRIANGLE_STRIP;

            default -> foundMode = VertexFormat.DrawMode.QUADS;
        }

        this.begin(foundMode, VertexFormats.POSITION_COLOR);
    }

    @Override
    public void vertex(IBridge.MatrixStack matrices, float x, float y, float z, int color) {
        ((BufferBuilder) (Object) this).vertex(((MatrixStack) matrices).peek().getPositionMatrix(), x, y, z).color(color).next();
    }

    @Override
    public void draw() {
        BufferRenderer.drawWithGlobalProgram(this.end());
        RenderSystem.disableBlend();
    }
}

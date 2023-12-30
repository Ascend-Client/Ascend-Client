package io.github.betterclient.client.util.cookeymod;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public interface OverlayRendered<T> {
    void renderWithOverlay(MatrixStack poseStack, VertexConsumerProvider multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l, int overlayCoords);
}
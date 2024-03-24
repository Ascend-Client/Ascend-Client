package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public interface FluidRenderHandler {
	Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state);

	default int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return -1;
	}

	default void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
		((FluidRenderHandlerRegistryImpl) FluidRenderHandlerRegistry.INSTANCE).renderFluid(pos, world, vertexConsumer, blockState, fluidState);
	}

	default void reloadTextures(SpriteAtlasTexture textureAtlas) {
	}
}
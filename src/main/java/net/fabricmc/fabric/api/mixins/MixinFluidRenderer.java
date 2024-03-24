package net.fabricmc.fabric.api.mixins;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistryImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {
	@Final
	@Shadow
	private Sprite[] lavaSprites;
	@Final
	@Shadow
	private Sprite[] waterSprites;
	@Shadow
	private Sprite waterOverlaySprite;

	private final ThreadLocal<Boolean> fabric_customRendering = ThreadLocal.withInitial(() -> false);
	private final ThreadLocal<Block> fabric_neighborBlock = new ThreadLocal<>();

	@Inject(at = @At("RETURN"), method = "onResourceReload")
	public void onResourceReloadReturn(CallbackInfo info) {
		FluidRenderer self = (FluidRenderer) (Object) this;
		((FluidRenderHandlerRegistryImpl) FluidRenderHandlerRegistry.INSTANCE).onFluidRendererReload(self, waterSprites, lavaSprites, waterOverlaySprite);
	}

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void tesselate(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
		if (!fabric_customRendering.get()) {
			// Prevent recursively looking up custom fluid renderers when default behavior is being invoked
			try {
				fabric_customRendering.set(true);
				tessellateViaHandler(view, pos, vertexConsumer, blockState, fluidState, info);
			} finally {
				fabric_customRendering.set(false);
			}
		}

		if (info.isCancelled()) {
			return;
		}
	}

	@Unique
	private void tessellateViaHandler(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
		FluidRenderHandler handler = ((FluidRenderHandlerRegistryImpl) FluidRenderHandlerRegistry.INSTANCE).getOverride(fluidState.getFluid());

		if (handler != null) {
			handler.renderFluid(pos, view, vertexConsumer, blockState, fluidState);
			info.cancel();
		}
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"), method = "render")
	public Block getOverlayBlock(BlockState state) {
		Block block = state.getBlock();
		fabric_neighborBlock.set(block);

		// An if-statement follows, we don't want this anymore and 'null' makes
		// its condition always false (due to instanceof)
		return null;
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.BY, by = 2), method = "render", ordinal = 0)
	public Sprite modSideSpriteForOverlay(Sprite chk) {
		Block block = fabric_neighborBlock.get();

		if (FluidRenderHandlerRegistry.INSTANCE.isBlockTransparent(block)) {
			return waterOverlaySprite;
		}

		return chk;
	}
}
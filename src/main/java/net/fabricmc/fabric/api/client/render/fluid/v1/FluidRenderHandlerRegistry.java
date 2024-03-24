package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;

public interface FluidRenderHandlerRegistry {
    FluidRenderHandlerRegistry INSTANCE = new FluidRenderHandlerRegistryImpl();

    FluidRenderHandler get(Fluid fluid);

    void register(Fluid fluid, FluidRenderHandler renderer);

    default void register(Fluid still, Fluid flow, FluidRenderHandler renderer) {
        register(still, renderer);
        register(flow, renderer);
    }

    void setBlockTransparency(Block block, boolean transparent);
    boolean isBlockTransparent(Block block);
}
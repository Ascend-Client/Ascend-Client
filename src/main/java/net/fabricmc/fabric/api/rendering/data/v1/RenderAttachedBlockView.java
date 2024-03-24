package net.fabricmc.fabric.api.rendering.data.v1;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public interface RenderAttachedBlockView extends BlockRenderView {
    @Nullable
    default Object getBlockEntityRenderAttachment(BlockPos pos) {
        BlockEntity be = this.getBlockEntity(pos);
        return be == null ? null : ((RenderAttachmentBlockEntity) be).getRenderAttachmentData();
    }
}

package net.fabricmc.fabric.api.rendering.data.v1;

import org.jetbrains.annotations.Nullable;

public interface RenderAttachmentBlockEntity {
    @Deprecated
    @Nullable
    Object getRenderAttachmentData();
}

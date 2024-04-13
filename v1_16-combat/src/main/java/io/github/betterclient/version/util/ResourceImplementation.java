package io.github.betterclient.version.util;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class ResourceImplementation implements Resource {
    public final IBridge.ResourceSupplier supplier;
    public ResourceImplementation(IBridge.ResourceSupplier sup) {
        supplier = sup;
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return supplier.getInputStream();
    }

    @Nullable
    @Override
    public <T> T getMetadata(ResourceMetadataReader<T> metaReader) {
        return null;
    }

    @Override
    public String getResourcePackName() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}

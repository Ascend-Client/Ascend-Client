package io.github.betterclient.version.mixin.client.fixes;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.FileNotFoundException;
import java.util.Optional;

@Mixin(ResourceFactory.class)
public interface MixinReloadableResourceManager {
    @Shadow
    Optional<Resource> getResource(Identifier id);

    /**
     * @author betterclient
     * @reason idk
     */
    @Overwrite
    default Resource getResourceOrThrow(Identifier id) throws FileNotFoundException {
        Optional<Resource> of = this.getResource(id);

        if(of.isEmpty()) {
            IBridge.Identifier identifier = new IBridge.Identifier(id);
            IBridge.Resource resource = BallSack.getInstance().findLoadedResource(identifier);

            if(resource != null) {
                return new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), resource.resourceSupplier::getInputStream);
            }
        } else {
            return of.get();
        }

        throw new FileNotFoundException();
    }
}

package io.github.betterclient.version.mixin.bridge.entity;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements IBridge.PlayerEntity {
    @Shadow @Final private PlayerInventory inventory;

    @Override
    public IBridge.ItemStack getArmorStack(int num) {
        ItemStack real = this.inventory.getArmorStack(num);
        return new IBridge.ItemStack(real, real.isDamageable(), real.getDamage(), real.getMaxDamage(), real.isStackable());
    }

    @Override
    public String getBiome() {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos blockPos = ((PlayerEntity) (Object) this).getBlockPos();
        String biomeName;

        if(blockPos.getY() >= client.world.getBottomY() && blockPos.getY() < client.world.getTopY()) {
            biomeName = client.world.getBiome(blockPos).getKeyOrValue().map((biomeKey) -> biomeKey.getValue().toString(), (biomes_) -> "[unregistered " + biomes_ + "]");
        } else {
            biomeName = "Outside";
        }

        return biomeName;
    }

    @Override
    public String getFacing() {
        Direction direction = ((PlayerEntity) (Object) this).getHorizontalFacing();
        return switch (direction) {
            case NORTH -> "North";
            case SOUTH -> "South";
            case WEST -> "West";
            case EAST -> "East";
            default -> "Invalid";
        };
    }

    private static String getBiomeString(RegistryEntry<Biome> biome) {
        return biome.getKeyOrValue().map((biomeKey) -> biomeKey.getValue().toString(), (biomes_) -> "[unregistered " + biomes_ + "]");
    }
}

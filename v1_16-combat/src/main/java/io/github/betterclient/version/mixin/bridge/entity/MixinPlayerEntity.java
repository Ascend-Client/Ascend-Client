package io.github.betterclient.version.mixin.bridge.entity;

import io.github.betterclient.client.bridge.IBridge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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
}

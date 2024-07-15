package io.github.betterclient.version.mixin.client.entity;

import io.github.betterclient.client.mod.impl.other.CrystalOptimizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public abstract class MixinEndCrystal extends Entity {
    private MixinEndCrystal() {
        super(null, null);
        throw new AssertionError("Life is hard, but initializing @Mixin is harder.");
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void hcscr$hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(!CrystalOptimizer.get().isToggled()) return;
        if (!explodesClientSide((EndCrystalEntity) (Object) this, source, amount)) return;

        remove(RemovalReason.KILLED);
        cir.setReturnValue(true);
    }

    public boolean explodesClientSide(EndCrystalEntity crystal, DamageSource source, float amount) {
        if (!crystal.getWorld().isClient || crystal.isRemoved() ||
                crystal.isInvulnerableTo(source) || source.getSource() instanceof EnderDragonEntity || amount <= 0) return false;

        if (source.getSource() instanceof PlayerEntity player) {
            if (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) <= 0) return false;
            AttributeContainer map = player.getAttributes();
            for (StatusEffectInstance instance : player.getStatusEffects()) {
                instance.getEffectType().value().onApplied(player, instance.getAmplifier());
            }
            amount = Math.min(amount, (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            for (StatusEffectInstance instance : player.getStatusEffects()) {
                instance.getEffectType().value().onRemoved(map);
            }
            return amount > 0;
        }

        return true;
    }
}
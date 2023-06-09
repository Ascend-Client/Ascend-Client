package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.NumberSetting;
import io.netty.util.AttributeMap;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

public class CrystalOptimizer extends Module {
    public CrystalOptimizer() {
        super("CrystalOptimizer", Category.OTHER);
    }

    public static CrystalOptimizer get() {
        return (CrystalOptimizer) BallSack.getInstance().moduleManager.getModuleByName("CrystalOptimizer");
    }

    public boolean explodesClientSide(EndCrystalEntity crystal, DamageSource source, float amount) {
        if (!this.toggled || !crystal.world.isClient || crystal.removed ||
                crystal.isInvulnerableTo(source) || source.getSource() instanceof EnderDragonEntity || amount <= 0) return false;

        if (source.getSource() instanceof PlayerEntity player) {
            if (player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) <= 0) return false;
            AttributeContainer map = player.getAttributes();
            for (StatusEffectInstance instance : player.getStatusEffects()) {
                instance.getEffectType().onApplied(player, map, instance.getAmplifier());
            }
            amount = Math.min(amount, (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            for (StatusEffectInstance instance : player.getStatusEffects()) {
                instance.getEffectType().onRemoved(player, map, instance.getAmplifier());
            }
            return amount > 0;
        }

        return true;
    }


}

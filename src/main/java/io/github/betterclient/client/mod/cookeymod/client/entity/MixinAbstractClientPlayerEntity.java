package io.github.betterclient.client.mod.cookeymod.client.entity;

import com.mojang.authlib.GameProfile;
import io.github.betterclient.client.mod.cookeymod.entity.LivingEntityAccessor;
import io.github.betterclient.client.mod.impl.other.CookeyMod;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {
    @Unique
    public BooleanSetting disableEffectBasedFovChange = CookeyMod.get().disableEffectBasedFovChange;

    public MixinAbstractClientPlayerEntity(World level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Redirect(method = "getSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"))
    public double disableEffectBasedFov(AbstractClientPlayerEntity player, EntityAttribute attribute) {
        if (this.disableEffectBasedFovChange.isValue()) {
            double mov = player.getAttributeBaseValue(attribute);
            if (this.isSprinting() && LivingEntityAccessor.SPRINTING_SPEED_BOOST() != null) {
                mov *= 1 + LivingEntityAccessor.SPRINTING_SPEED_BOOST().getValue();
            }
            return mov;
        }
        else {
            return player.getAttributeValue(attribute);
        }
    }
}

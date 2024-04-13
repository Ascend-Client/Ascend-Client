package io.github.betterclient.version.mixin.cookeymod.client.network;

import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.version.mods.CookeyMod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow
    private ClientWorld world;

    @Unique
    BooleanSetting enableDamageCameraTilt = CookeyMod.get().enableDamageCameraTilt;

    @Inject(method = "onVelocityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocityClient(DDD)V"))
    public void injectHurtDir(EntityVelocityUpdateS2CPacket par1, CallbackInfo ci) {
        Entity entity = this.world.getEntityById(par1.getId());
        if (entity instanceof LivingEntity livingEntity) {
            if (this.enableDamageCameraTilt.isValue()) {
                livingEntity.knockbackVelocity = (float) (Math.atan2(par1.getVelocityZ() / 8000D, par1.getVelocityX() / 8000D) * 57.2957763671875D - (double) livingEntity.yaw);
            }
        }
    }
}

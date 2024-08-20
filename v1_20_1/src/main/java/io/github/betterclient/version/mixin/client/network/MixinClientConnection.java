package io.github.betterclient.version.mixin.client.network;

import io.github.betterclient.client.mod.impl.other.CrystalOptimizer;
import io.github.betterclient.version.access.PlayerInteractEntityC2SPacketAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet instanceof PlayerInteractEntityC2SPacket interactPacket) {
            interactPacket.handle(new PlayerInteractEntityC2SPacket.Handler() {

                @Override
                public void interact(Hand hand) {

                }

                @Override
                public void interactAt(Hand hand, Vec3d pos) {

                }

                @Override
                public void attack() {
                    Entity entity;
                    HitResult hitResult = client.crosshairTarget;
                    if (hitResult == null) {
                        return;
                    }
                    if (
                            hitResult.getType() == HitResult.Type.ENTITY &&
                                    (entity = ((EntityHitResult) hitResult).getEntity()) instanceof EndCrystalEntity &&
                                    CrystalOptimizer.get().isToggled() && ((PlayerInteractEntityC2SPacketAccessor) packet).isAttack() &&
                                    !client.player.isSpectator()) {

                        StatusEffectInstance weakness = client.player.getStatusEffect(StatusEffects.WEAKNESS);
                        StatusEffectInstance strength = client.player.getStatusEffect(StatusEffects.STRENGTH);
                        if (!(weakness == null || strength != null && strength.getAmplifier() > weakness.getAmplifier() || MixinClientConnection.this.isTool(client.player.getMainHandStack()))) {
                            return;
                        }
                        entity.kill();
                    }
                }
            });
        }
    }

    private boolean isTool(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ToolItem tiered) || itemStack.getItem() instanceof HoeItem) {
            return false;
        }
        ToolMaterial material = tiered.getMaterial();
        return material == ToolMaterials.DIAMOND || material == ToolMaterials.NETHERITE;
    }
}
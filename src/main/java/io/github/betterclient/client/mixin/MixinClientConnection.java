package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.CrystalOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (packet instanceof PlayerInteractEntityC2SPacket interactPacket) {
            interactPacket.apply(new ServerPlayPacketListener() {
                @Override
                public void onHandSwing(HandSwingC2SPacket packet) {

                }

                @Override
                public void onGameMessage(ChatMessageC2SPacket packet) {

                }

                @Override
                public void onClientStatus(ClientStatusC2SPacket packet) {

                }

                @Override
                public void onClientSettings(ClientSettingsC2SPacket packet) {

                }

                @Override
                public void onConfirmTransaction(ConfirmGuiActionC2SPacket packet) {

                }

                @Override
                public void onButtonClick(ButtonClickC2SPacket packet) {

                }

                @Override
                public void onClickWindow(ClickWindowC2SPacket packet) {

                }

                @Override
                public void onCraftRequest(CraftRequestC2SPacket packet) {

                }

                @Override
                public void onGuiClose(GuiCloseC2SPacket packet) {

                }

                @Override
                public void onCustomPayload(CustomPayloadC2SPacket packet) {

                }

                @Override
                public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
                    Entity entity;
                    HitResult hitResult = client.crosshairTarget;
                    if (hitResult == null) {
                        return;
                    }
                    if (
                            hitResult.getType() == HitResult.Type.ENTITY &&
                            (entity = ((EntityHitResult) hitResult).getEntity()) instanceof EndCrystalEntity &&
                            CrystalOptimizer.get().isToggled() && packet.getType().equals(PlayerInteractEntityC2SPacket.InteractionType.ATTACK) &&
                            !client.player.isSpectator()) {

                        StatusEffectInstance weakness = client.player.getStatusEffect(StatusEffects.WEAKNESS);
                        StatusEffectInstance strength = client.player.getStatusEffect(StatusEffects.STRENGTH);
                        if (!(weakness == null || strength != null && strength.getAmplifier() > weakness.getAmplifier() || MixinClientConnection.this.isTool(client.player.getMainHandStack()))) {
                            return;
                        }
                        entity.kill();
                    }
                }

                @Override
                public void onKeepAlive(KeepAliveC2SPacket packet) {

                }

                @Override
                public void onPlayerMove(PlayerMoveC2SPacket packet) {

                }

                @Override
                public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {

                }

                @Override
                public void onPlayerAction(PlayerActionC2SPacket packet) {

                }

                @Override
                public void onClientCommand(ClientCommandC2SPacket packet) {

                }

                @Override
                public void onPlayerInput(PlayerInputC2SPacket packet) {

                }

                @Override
                public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {

                }

                @Override
                public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {

                }

                @Override
                public void onSignUpdate(UpdateSignC2SPacket packet) {

                }

                @Override
                public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {

                }

                @Override
                public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {

                }

                @Override
                public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {

                }

                @Override
                public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {

                }

                @Override
                public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {

                }

                @Override
                public void onVehicleMove(VehicleMoveC2SPacket packet) {

                }

                @Override
                public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {

                }

                @Override
                public void onRecipeBookData(RecipeBookDataC2SPacket packet) {

                }

                @Override
                public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {

                }

                @Override
                public void onAdvancementTab(AdvancementTabC2SPacket packet) {

                }

                @Override
                public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {

                }

                @Override
                public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {

                }

                @Override
                public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {

                }

                @Override
                public void onPickFromInventory(PickFromInventoryC2SPacket packet) {

                }

                @Override
                public void onRenameItem(RenameItemC2SPacket packet) {

                }

                @Override
                public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {

                }

                @Override
                public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket packet) {

                }

                @Override
                public void onMerchantTradeSelect(SelectMerchantTradeC2SPacket packet) {

                }

                @Override
                public void onBookUpdate(BookUpdateC2SPacket packet) {

                }

                @Override
                public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {

                }

                @Override
                public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {

                }

                @Override
                public void onJigsawUpdate(UpdateJigsawC2SPacket packet) {

                }

                @Override
                public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {

                }

                @Override
                public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {

                }

                @Override
                public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {

                }

                @Override
                public void onDisconnected(Text reason) {

                }

                @Override
                public ClientConnection getConnection() {
                    return null;
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
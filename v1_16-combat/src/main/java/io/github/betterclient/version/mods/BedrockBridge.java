package io.github.betterclient.version.mods;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

public class BedrockBridge extends Module {
    public MinecraftClient client;

    public BedrockBridge() {
        super("Bedrock Bridging", Category.OTHER, null);
        this.client = MinecraftClient.getInstance();
    }

    public static boolean isEnabled() {
        return get().toggled;
    }

    public static BedrockBridge get() {
        return (BedrockBridge) BallSack.getInstance().moduleManager.getModuleByName("Bedrock Bridging");
    }

    public void checkReachAroundAndExecute(Hand hand, ItemStack itemStack) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null)
            return;
        int count = itemStack.getCount();
        Vector3f facing = player.getHorizontalFacing().getUnitVector();
        if (canReachAround()) {
            BlockHitResult blockHitResult;
            if(isNonFullBlock()){
                blockHitResult = new BlockHitResult(player.getPos().add(facing.getX(), facing.getY()-1, facing.getZ()), Direction.fromVector((int) -facing.getX(), 0, (int) -facing.getZ()), player.getBlockPos().offset(player.getHorizontalFacing()), false);
            }else{
                blockHitResult = new BlockHitResult(player.getPos().add(facing.getX(), facing.getY(), facing.getZ()), Direction.fromVector((int) -facing.getX(), 0, (int) -facing.getZ()), player.getBlockPos().down().offset(player.getHorizontalFacing()), false);
            }
            ActionResult result = client.interactionManager.interactBlock(player, client.world, hand, blockHitResult);
            if (result.isAccepted()) {
                if (result.shouldSwingHand()) {
                    player.swingHand(hand);
                    if (!itemStack.isEmpty() && (itemStack.getCount() != count || client.interactionManager.hasCreativeInventory())) {
                        client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                    }
                }
            }
        }
    }

    private boolean canReachAround() {
        if (client.player == null || client.world == null || client.crosshairTarget == null)
            return false;
        return client.player.pitch > 44.5 && (!(client.world.getBlockState(client.player.getBlockPos().down()).isAir() || client.world.getBlockState(client.player.getBlockPos().down()).getBlock() instanceof FluidBlock) || isNonFullBlock()) && client.crosshairTarget.getType().equals(HitResult.Type.MISS) && checkRelativeBlockPosition() && ((client.world.getBlockState(client.player.getBlockPos().down().offset(client.player.getHorizontalFacing())).getBlock() instanceof FluidBlock) || (client.world.getBlockState(client.player.getBlockPos().down().offset(client.player.getHorizontalFacing())).getBlock() instanceof AirBlock));
    }

    private boolean isNonFullBlock(){
        if(client.world == null || client.player == null)
            return false;
        Block playerPosBlock = client.world.getBlockState(client.player.getBlockPos()).getBlock();
        return playerPosBlock instanceof SlabBlock ||
                playerPosBlock instanceof StairsBlock ||
                playerPosBlock instanceof ChainBlock ||
                playerPosBlock instanceof EndRodBlock ||
                playerPosBlock instanceof  BedBlock ||
                playerPosBlock instanceof  SkullBlock ||
                playerPosBlock instanceof  StonecutterBlock ||
                playerPosBlock instanceof AbstractChestBlock;
    }

    private boolean checkRelativeBlockPosition() {
        if (client.player == null)
            return false;
        return checkRelativeBlockPosition((client.player.getPos().getX() - client.player.getBlockPos().getX()), client.player.getHorizontalFacing().getUnitVector().getX()) || checkRelativeBlockPosition((client.player.getPos().getZ() - client.player.getBlockPos().getZ()), client.player.getHorizontalFacing().getUnitVector().getZ());
    }

    private boolean checkRelativeBlockPosition(double pos, float direction) {
        double distance = 0.7d;

        if (direction > 0) {
            return 1-pos < distance;
        } else if (direction < 0) {
            return pos < distance;
        }
        return false;
    }
}

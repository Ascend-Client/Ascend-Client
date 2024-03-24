package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorStatusMod extends HUDModule {
    public BooleanSetting leftToRight = new BooleanSetting("Left To Right", false);
    public BooleanSetting renderDurability = new BooleanSetting("Render Durability", false);

    public ArmorStatusMod() {
        super("Armor Status", 20, 50);
        this.addSetting(leftToRight);
        this.addSetting(renderDurability);

        this.getSettings().remove(this.textColor);
    }

    @Override
    public void render(Renderable r) {
        ItemStack helm, chest, legging, boots;

        if(MinecraftClient.getInstance().player == null) {
            helm = new ItemStack(Items.DIAMOND_HELMET);
            chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
            legging = new ItemStack(Items.DIAMOND_LEGGINGS);
            boots = new ItemStack(Items.DIAMOND_BOOTS);
        } else {
            helm = MinecraftClient.getInstance().player.getInventory().getArmorStack(3);
            chest = MinecraftClient.getInstance().player.getInventory().getArmorStack(2);
            legging = MinecraftClient.getInstance().player.getInventory().getArmorStack(1);
            boots = MinecraftClient.getInstance().player.getInventory().getArmorStack(0);
        }

        if(leftToRight.isValue()){
            r.renderItemStack(60, 0, helm, renderDurability.isValue());
            r.renderItemStack(40, 0, chest, renderDurability.isValue());
            r.renderItemStack(20, 0, legging, renderDurability.isValue());
            r.renderItemStack(0, 0, boots, renderDurability.isValue());
        }else{
            r.renderItemStack(0, 0, helm, renderDurability.isValue());
            r.renderItemStack(0, 20, chest, renderDurability.isValue());
            r.renderItemStack(0, 40, legging, renderDurability.isValue());
            r.renderItemStack(0, 60, boots, renderDurability.isValue());
        }
    }
}

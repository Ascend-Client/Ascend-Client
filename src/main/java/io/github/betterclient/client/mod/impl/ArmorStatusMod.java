package io.github.betterclient.client.mod.impl;

import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class ArmorStatusMod extends HUDModule {
    public BooleanSetting leftToRight = new BooleanSetting("Left To Right", false);
    public BooleanSetting renderDurability = new BooleanSetting("Render Durability", false);

    public ArmorStatusMod() {
        super("Armor Status", 20, 50);
        this.addSetting(leftToRight);
        this.addSetting(renderDurability);
    }

    @Override
    public void render(Renderable r) {
        assert MinecraftClient.getInstance().player != null;
        ItemStack helm = MinecraftClient.getInstance().player.inventory.getArmorStack(3);
        ItemStack chest = MinecraftClient.getInstance().player.inventory.getArmorStack(2);
        ItemStack legging = MinecraftClient.getInstance().player.inventory.getArmorStack(1);
        ItemStack boots = MinecraftClient.getInstance().player.inventory.getArmorStack(0);

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

package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;

public class ArmorStatusMod extends HUDModule {
    public BooleanSetting leftToRight = new BooleanSetting("Left To Right", false);
    public BooleanSetting renderDurability = new BooleanSetting("Render Durability", false);

    public ArmorStatusMod() {
        super("Armor Status", 20, 50, new IBridge.Identifier("minecraft:textures/ballsack/modules/armor.png"));
        this.addSetting(leftToRight);
        this.addSetting(renderDurability);

        this.getSettings().remove(this.textColor);
        this.getSettings().remove(this.size);
    }

    @Override
    public void render(Renderable r) {
        ItemStack helm, chest, legging, boots;

        if(MinecraftClient.getInstance().getPlayer() == null) {
            helm = IBridge.getInstance().getClient().getItems().DIAMOND_HELMET;
            chest = IBridge.getInstance().getClient().getItems().DIAMOND_CHESTPLATE;
            legging = IBridge.getInstance().getClient().getItems().DIAMOND_LEGGINGS;
            boots = IBridge.getInstance().getClient().getItems().DIAMOND_BOOTS;
        } else {
            helm = MinecraftClient.getInstance().getPlayer().getArmorStack(3);
            chest = MinecraftClient.getInstance().getPlayer().getArmorStack(2);
            legging = MinecraftClient.getInstance().getPlayer().getArmorStack(1);
            boots = MinecraftClient.getInstance().getPlayer().getArmorStack(0);
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

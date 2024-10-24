package io.github.betterclient.version.mods;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.mod.setting.ColorSetting;
import io.github.betterclient.client.mod.setting.NoneSetting;
import io.github.betterclient.client.mod.setting.NumberSetting;
import io.github.betterclient.version.util.cookeymod.OverlayReloadListener;
import net.minecraft.client.MinecraftClient;

import java.awt.*;

public class CookeyMod extends Module {
    public NoneSetting text1 = new NoneSetting("Animations");
    public BooleanSetting swingAndUseItem = new BooleanSetting("Swing and use item", false);
    public BooleanSetting oldSwing = new BooleanSetting("Old swing animation", false);
    public NumberSetting sneakAnimationSpeed = new NumberSetting("Sneak animation speed", 100, 0, 200);
    public BooleanSetting disableCameraBobbing = new BooleanSetting("Disable camera bobbing", false);
    public BooleanSetting enableToolBlocking = new BooleanSetting("Enable tool blocking", false);
    public BooleanSetting shieldlessToolBlocking = new BooleanSetting("Enable tool blocking (no shield)", false);
    public BooleanSetting showEatingInThirdPerson = new BooleanSetting("Show eating in third person", false);
    public BooleanSetting enableDamageCameraTilt = new BooleanSetting("Enable damage camera tilt", false);

    public NoneSetting text2 = new NoneSetting("Hud Rendering");
    public NumberSetting attackCooldownHandOffset = new NumberSetting("Attack cooldown hand offset", 0, -100, 100);
    public ColorSetting damageColor = new ColorSetting("Damage Color", new Color(255, 0, 0, 77)) {
        @Override
        public void setColor(Color color) {
            super.setColor(color);
            OverlayReloadListener.callEvent();
        }
    };
    public BooleanSetting showDamageTintOnArmor = new BooleanSetting("Show damage tilt on armor", false);
    public BooleanSetting onlyShowShieldWhenBlocking = new BooleanSetting("Only show shield when blocking", false);
    public BooleanSetting disableEffectBasedFovChange = new BooleanSetting("Disable effect based fov change", false);
    public BooleanSetting alternativeBobbing = new BooleanSetting("Alternative bobbing (broken)", false);

    public NoneSetting text3 = new NoneSetting("Miscellaneous");
    public BooleanSetting renderOwnName = new BooleanSetting("Render Own Name On 3rd Person", false);
    public BooleanSetting force100PercentRecharge = new BooleanSetting("Force 100% Recharge", false);

    public CookeyMod() {
        super("CookeyMod", Category.OTHER, null);
        this.addSetting(text1);
        this.addSetting(swingAndUseItem);
        this.addSetting(oldSwing);
        this.addSetting(sneakAnimationSpeed);
        this.addSetting(disableCameraBobbing);
        this.addSetting(enableToolBlocking);
        this.addSetting(shieldlessToolBlocking);
        this.addSetting(showEatingInThirdPerson);
        this.addSetting(enableDamageCameraTilt);

        this.addSetting(text2);
        this.addSetting(attackCooldownHandOffset);
        this.addSetting(damageColor);
        this.addSetting(showDamageTintOnArmor);
        this.addSetting(onlyShowShieldWhenBlocking);
        this.addSetting(disableEffectBasedFovChange);
        //this.addSetting(alternativeBobbing);

        this.addSetting(text3);
        this.addSetting(renderOwnName);
        //this.addSetting(force100PercentRecharge);
    }

    public static CookeyMod get() {
        return (CookeyMod) Ascend.getInstance().moduleManager.getModuleByName("CookeyMod");
    }

    public static boolean isBlockingRightClick() {
        MinecraftClient client = MinecraftClient.getInstance();

        return client.options.keyUse.isPressed();
    }
}
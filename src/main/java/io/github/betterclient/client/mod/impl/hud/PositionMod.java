package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;
import io.github.betterclient.client.util.UIUtil;

public class PositionMod extends HUDModule {
    public BooleanSetting showBiome = new BooleanSetting("Show Biome", true);
    public BooleanSetting showFacing = new BooleanSetting("Show Direction", true);

    public PositionMod() {
        super("Position", 10, 10, new IBridge.Identifier("minecraft:textures/ascend/modules/cps.png"));
        this.addSetting(showBiome);
        this.addSetting(showFacing);
    }

    @Override
    public void render(Renderable renderable) {
        IBridge.PlayerEntity entity = IBridge.MinecraftClient.getInstance().getPlayer();
        if(entity == null) {
            renderable.renderText("X: " + 100, 0, 0, textColor.getColor())
                    .renderText("Y: " + 50, 0, 10, textColor.getColor())
                    .renderText("Z: " + 200, 0, 20, textColor.getColor());

            int cy = 30;

            if(showBiome.value) {
                renderable.renderText("Biome: Plains", 0, cy, textColor.getColor());
                cy+=10;
            }
            if(showFacing.value) {
                renderable.renderText("Facing: North", 0, cy, textColor.getColor());
            }
            return;
        }

        IBridge.Vec3d pos = entity.getPos();
        renderable.renderText("X: " + ((int) pos.x()), 0, 0, textColor.getColor())
                .renderText("Y: " + ((int) pos.y()), 0, 10, textColor.getColor())
                .renderText("Z: " + ((int) pos.z()), 0, 20, textColor.getColor());

        int cy = 30;

        if(showBiome.value) {
            renderable.renderText("Biome: " + UIUtil.capitalize(entity.getBiome().replace("minecraft:", "")), 0, cy, textColor.getColor());
            cy+=10;
        }
        if(showFacing.value) {
            renderable.renderText("Facing: " + entity.getFacing(), 0, cy, textColor.getColor());
        }
    }
}

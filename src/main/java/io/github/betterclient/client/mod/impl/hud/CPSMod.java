package io.github.betterclient.client.mod.impl.hud;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.event.EventTarget;
import io.github.betterclient.client.event.impl.MouseEvent;
import io.github.betterclient.client.mod.HUDModule;
import io.github.betterclient.client.mod.Renderable;
import io.github.betterclient.client.mod.setting.BooleanSetting;

import java.util.List;
import java.util.Vector;

public class CPSMod extends HUDModule {
    public List<Long> rightClick = new Vector<>();
    public List<Long> leftClick = new Vector<>();

    public BooleanSetting showBoth = new BooleanSetting("Show Both Keys", true);

    public CPSMod() {
        super("CPS", 100, 10, new IBridge.Identifier("minecraft:textures/ascend/modules/cps.png"));
        this.addSetting(showBoth);
    }

    @Override
    public void render(Renderable renderable) {
        leftClick.removeIf(aLong -> aLong + 1000 < System.currentTimeMillis());
        rightClick.removeIf(aLong -> aLong + 1000 < System.currentTimeMillis());

        String cpsText = leftClick.size() + " " + (showBoth.isValue() ? "| " + rightClick.size() + " " : "") + "CPS";
        renderable.renderText(cpsText, 0, 0, this.textColor.getColor());
    }

    @EventTarget
    public void onMouse(MouseEvent ev) {
        if(ev.state) {
            if(ev.button == 0) {
                leftClick.add(System.currentTimeMillis());
            }
            if(ev.button == 1) {
                rightClick.add(System.currentTimeMillis());
            }
        }
    }
}

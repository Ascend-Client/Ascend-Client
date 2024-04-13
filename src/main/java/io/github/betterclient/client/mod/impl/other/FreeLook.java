package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.KeyBindSetting;

public class FreeLook extends Module {
    public boolean perspectiveToggled = false;
    public Perspective previousPerspective = Perspective.FIRST_PERSON; //prev f5 state

    public KeyBindSetting bind = new KeyBindSetting("FreeLook Keybind", IBridge.getKeys().KEY_ALT, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.perspectiveToggled = true;
        this.previousPerspective = client.getOptions().getPerspective();

        client.getOptions().setPerspective(Perspective.THIRD_PERSON_BACK);
    }, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.perspectiveToggled = false;

        client.getOptions().setPerspective(previousPerspective);
    });

    public FreeLook() {
        super("FreeLook", Category.OTHER);
        this.addSetting(this.bind);
    }

    public static FreeLook get() {
        return (FreeLook) BallSack.getInstance().moduleManager.getModuleByName("FreeLook");
    }
}

package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.KeyBindSetting;
import io.github.betterclient.client.mod.setting.ModeSetting;

public class FreeLook extends Module {
    public boolean perspectiveToggled = false;
    public Perspective previousPerspective = Perspective.FIRST_PERSON;//prev f5 state
    public ModeSetting perspectiveMode = new ModeSetting("FreeLook perspective", "Third Person Back", "Third Person Back", "Third Person Front", "First Person");

    public KeyBindSetting bind = new KeyBindSetting("FreeLook Keybind", IBridge.getKeys().KEY_ALT, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.perspectiveToggled = true;
        this.previousPerspective = client.getOptions().getPerspective();

        client.getOptions().setPerspective(perspective());
    }, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.perspectiveToggled = false;

        client.getOptions().setPerspective(previousPerspective);
    });

    private Perspective perspective() {
        return switch (perspectiveMode.value) {
            case "Third Person Front" -> Perspective.THIRD_PERSON_FRONT;
            case "First Person" -> Perspective.FIRST_PERSON;
            default -> Perspective.THIRD_PERSON_BACK;
        };

    }

    public FreeLook() {
        super("FreeLook", Category.OTHER, null);
        this.addSetting(this.bind);
        this.addSetting(perspectiveMode);
    }

    public static FreeLook get() {
        return (FreeLook) Ascend.getInstance().moduleManager.getModuleByName("FreeLook");
    }
}

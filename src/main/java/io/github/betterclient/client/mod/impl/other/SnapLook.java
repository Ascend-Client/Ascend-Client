package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.bridge.IBridge.MinecraftClient;
import io.github.betterclient.client.bridge.IBridge.Perspective;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.KeyBindSetting;
import io.github.betterclient.client.mod.setting.ModeSetting;

public class SnapLook extends Module {
    public Perspective previousPerspective = Perspective.FIRST_PERSON;//prev f5 state
    public ModeSetting perspectiveMode = new ModeSetting("SnapLook perspective", "Third Person Back", "Third Person Back", "Third Person Front", "First Person");

    public KeyBindSetting bind = new KeyBindSetting("SnapLook Keybind", IBridge.getKeys().KEY_V, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.previousPerspective = client.getOptions().getPerspective();
        client.getOptions().setPerspective(perspective());
    }, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient.getInstance().getOptions().setPerspective(previousPerspective);
    });

    private Perspective perspective() {
        return switch (perspectiveMode.value) {
            case "Third Person Front" -> Perspective.THIRD_PERSON_FRONT;
            case "First Person" -> Perspective.FIRST_PERSON;
            default -> Perspective.THIRD_PERSON_BACK;
        };
    }

    public SnapLook() {
        super("SnapLook", Category.OTHER, null);
        this.addSetting(this.bind);
        this.addSetting(perspectiveMode);
    }

    public static SnapLook get() {
        return (SnapLook) BallSack.getInstance().moduleManager.getModuleByName("FreeLook");
    }
}

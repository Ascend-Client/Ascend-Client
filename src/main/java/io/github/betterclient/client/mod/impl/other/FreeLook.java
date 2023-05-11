package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.access.GameOptionsAccess;
import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.util.ClickableBind;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.Perspective;
import org.lwjgl.glfw.GLFW;

public class FreeLook extends Module {
    public boolean perspectiveToggled = false;
    public Perspective previousPerspective = Perspective.FIRST_PERSON; //prev f5 state

    public ClickableBind bind = ClickableBind.registerKeyBind(new ClickableBind("FreeLook", GLFW.GLFW_KEY_LEFT_ALT, "BallSack Client", () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.perspectiveToggled = true;
        this.previousPerspective = client.options.getPerspective();

        ((GameOptionsAccess) client.options)
                .setPerspective(Perspective.THIRD_PERSON_BACK);
    }, () -> {
        if(!this.isToggled())
            return;

        MinecraftClient client = MinecraftClient.getInstance();

        this.perspectiveToggled = false;

        ((GameOptionsAccess) client.options)
                .setPerspective(previousPerspective);
    }));

    public FreeLook() {
        super("FreeLook", Category.OTHER);
    }

    public static FreeLook get() {
        return (FreeLook) BallSack.getInstance().moduleManager.getModuleByName("FreeLook");
    }
}

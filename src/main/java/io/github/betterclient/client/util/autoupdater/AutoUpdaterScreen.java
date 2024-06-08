package io.github.betterclient.client.util.autoupdater;

import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.ui.minecraft.CustomLoadingOverlay;
import io.github.betterclient.client.ui.minecraft.CustomTitleMenu;
import io.github.betterclient.client.util.UIUtil;

import java.awt.*;

import static io.github.betterclient.client.util.UIUtil.*;

public class AutoUpdaterScreen extends Screen {
    public final Identifier chosenBackground;
    private final CustomTitleMenu parent;
    private float animatedMouseX, animatedMouseY;
    private int lastMouseX, lastMouseY;
    private final int bgcolor = new Color(0, 0, 0, 120).getRGB();

    public AutoUpdaterScreen(Identifier chosenBackground, CustomTitleMenu parent) {
        this.chosenBackground = chosenBackground;
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        animatedMouseX += ((mouseX-animatedMouseX) / 1.8) + 0.1;
        animatedMouseY += ((mouseY-animatedMouseY) / 1.8) + 0.1;
        MinecraftClient client = MinecraftClient.getInstance();

        fill(matrices, (int) animatedMouseX, (int) animatedMouseY, (int) (animatedMouseX + 2), (int) (animatedMouseY + 2), -1);

        int panX = (int) map(animatedMouseX, 0, width, -50, 0);
        int panY = (int) map(animatedMouseY, 0, height, -50, 0);

        client.setShaderTexture(0, chosenBackground);
        client.setShaderColor(1, 1, 1, 0.7f);
        drawTexture(matrices, panX, panY, 0, 0, width + 50, height + 50, width + 50, height + 50);

        String str = "There's an update available!";
        String str2 = "Would you like to update?";
        this.textRenderer.draw(matrices, str, (float) (width / 2 - (this.textRenderer.getWidth(str) / 2)), (float) (height*0.25), -1);
        this.textRenderer.draw(matrices, str2, (float) (width / 2 - (this.textRenderer.getWidth(str2) / 2)), (float) (height*0.25) + 20, -1);

        drawRoundedRect(width / 2 - 125, height*0.55, width / 2 - 25, height*0.55 + 20, 2f, bgcolor);
        float[] iPos = getIdealRenderingPosForText("Update", width / 2 - 125, height*0.55, width / 2 - 25, height*0.55 + 20);
        textRenderer.draw(matrices, "Update", iPos[0], iPos[1], -1);

        drawRoundedRect(width / 2 + 125, height*0.55, width / 2 + 25, height*0.55 + 20, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Don't update", width / 2 + 125, height*0.55, width / 2 + 25, height*0.55 + 20);
        textRenderer.draw(matrices, "Don't update", iPos[0], iPos[1], -1);

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    public boolean isMouseOn(double x, double y, double endX, double endY) {
        return UIUtil.basicCollisionCheck(
                lastMouseX, lastMouseY,
                x, y,
                endX, endY
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;
        MinecraftClient client = MinecraftClient.getInstance();
        if(button == 0) {
            int o = (int) (height*0.55);
            int o20 = o + 20;
            if(isMouseOn(width / 2 - 125, o, width / 2 - 25, o20)) {
                AutoUpdaterUtil.update();
            } else if(isMouseOn(width / 2 + 25, o, width / 2 + 125, o20)) {
                BallSack.getInstance().doUpdate = false;
                CustomLoadingOverlay.isDoingAnimation = false;
                client.setGuiScreen(this.parent);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

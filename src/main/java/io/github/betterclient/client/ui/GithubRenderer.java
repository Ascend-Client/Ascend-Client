package io.github.betterclient.client.ui;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.GithubMan;

import java.awt.*;

public class GithubRenderer {
    public static void render(Object screen, GithubMan man, IBridge.TextRenderer renderer, int height) {
        if(IBridge.MinecraftClient.getInstance().isCustomScreen(screen)) return;
        if(IBridge.MinecraftClient.getInstance().isChat(screen)) return;

        int x = 2;
        int y = height - 10;

        IBridge.MatrixStack matrices = IBridge.newMatrixStack();

        matrices.push();
        matrices.translate(x, y, 1);
        matrices.scale(0.8f, 0.8f, 1f);
        matrices.translate(-x, -y, 1);
        renderer.draw(matrices, "Ballsack Client " + man.commitId + "/" + man.branch, x, y, Color.gray.getRGB());
        matrices.pop();
    }
}

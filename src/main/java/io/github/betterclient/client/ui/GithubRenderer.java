package io.github.betterclient.client.ui;

import io.github.betterclient.client.util.GithubMan;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class GithubRenderer {
    public static void render(Screen screen, GithubMan man, TextRenderer renderer, int height) {
        int x = 2;
        int y = height - 10;

        MatrixStack matrices = new MatrixStack();

        matrices.push();
        matrices.translate(x, y, 1);
        matrices.scale(0.8f, 0.8f, 1f);
        matrices.translate(-x, -y, 1);
        renderer.draw(matrices, "Ballsack Client " + man.commitId + "/" + man.branch, x, y, Color.gray.getRGB());
        matrices.pop();
    }
}

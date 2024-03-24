package io.github.betterclient.client.ui.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.client.Application;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.ui.clickgui.HUDMoveUI;
import io.github.betterclient.client.util.UIUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;

import static io.github.betterclient.client.util.UIUtil.*;

public class CustomTitleMenu extends Screen {
    public boolean isFirstLaunch;
    public long startAnim, endAnim;


    private int lastMouseX, lastMouseY;
    private float animatedMouseX, animatedMouseY;
    private int buttonWallY = 0;
    private final int bgcolor = new Color(0, 0, 0, 84).getRGB();

    public CustomTitleMenu(boolean firstLaunch) {
        super(Text.of(""));
        this.isFirstLaunch = firstLaunch;
    }

    @Override
    protected void init() {
        startAnim = 0;
    }

    /**
     * decobra wanted this
     */
    private void playStartupSound() {
        try {
            File file = new File(Application.clientFolder, "launch.wav");
            if(!file.exists()) return;

            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Audio line is not supported.");
                return;
            }

            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;

            while ((bytesRead = ais.read(buffer, 0, buffer.length)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            line.drain();
            line.close();
            ais.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(startAnim == 0) {
            startAnim = System.currentTimeMillis();

            endAnim = System.currentTimeMillis() + 2000; //2 second animation
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        fill(matrices, 0, 0, width, height, Color.black.getRGB());

        int alpha = (int) map(System.currentTimeMillis() - startAnim, 0, 2000, 0, 150);
        fill(matrices, 0, 0, width, height, new Color(255, 255, 255, Math.max(120 - alpha, 0)).getRGB());

        int prepanY = (int) map(animatedMouseY, 0, height, -100, 0);

        //CustomLoadingOverlay.isFirst = false;
        int panoramaY = 0;
        if(CustomLoadingOverlay.isDoingAnimation && this.isFirstLaunch && System.currentTimeMillis() <= (endAnim + 250)) { //continue the animation for extra time so its smoother
            CustomLoadingOverlay.doRender = false;

            panoramaY = (int) map(System.currentTimeMillis(), startAnim, endAnim, width, prepanY);
            buttonWallY = (int) map(System.currentTimeMillis(), startAnim, endAnim, width, 0);
            if(System.currentTimeMillis() > endAnim) {
                panoramaY = prepanY;
                buttonWallY = 0;

                //run in seperate thread to not stop app
                new Thread(this::playStartupSound).start();
            }
        } else {
            panoramaY = 0;
            CustomLoadingOverlay.isDoingAnimation = false;
        }

        int panX = (int) map(animatedMouseX, 0, width, -100, 0);
        int panY = panoramaY;

        if(System.currentTimeMillis() > endAnim || !CustomLoadingOverlay.isDoingAnimation) {
            panY = (int) map(animatedMouseY, 0, height, -100, 0);
        }

        this.client.getTextureManager().bindTexture(new Identifier("textures/ballsack/background.png"));
        RenderSystem.enableBlend();
        RenderSystem.color4f(1, 1, 1, 1);
        drawTexture(matrices, panX, panY, 0, 0, width + 100, height + 100, width + 100, height + 100);
        RenderSystem.disableBlend();

        animatedMouseX += ((mouseX-animatedMouseX) / 1.8) + 0.1;
        animatedMouseY += ((mouseY-animatedMouseY) / 1.8) + 0.1;

        //a wall of buttons

        setStart(0, buttonWallY);

        drawRoundedRect(width / 2 - 100, height / 2 - 55, width / 2 + 100, height / 2 - 35, 2f, bgcolor);
        int[] iPos = getIdealRenderingPosForText("Singleplayer", width / 2 - 100, height / 2 - 55, width / 2 + 100, height / 2 - 35);
        textRenderer.draw(matrices, "Singleplayer", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2 - 100, height / 2 - 25, width / 2 + 100, height / 2 - 5, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Multiplayer", width / 2 - 100, height / 2 - 25, width / 2 + 100, height / 2 - 5);
        textRenderer.draw(matrices, "Multiplayer", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2 - 100, height / 2 + 5, width / 2 + 100, height / 2 + 25, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Ballsack Settings", width / 2 - 100, height / 2 + 5, width / 2 + 100, height / 2 + 25);
        textRenderer.draw(matrices, "Ballsack Settings", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2 - 100, height / 2 + 35, width / 2 - 5, height / 2 + 55, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Options", width / 2 - 100, height / 2 + 35, width / 2 - 5, height / 2 + 55);
        textRenderer.draw(matrices, "Options", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2 + 5, height / 2 + 35, width / 2 + 100, height / 2 + 55, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Quit", width / 2 + 5, height / 2 + 35, width / 2 + 100, height / 2 + 55);
        textRenderer.draw(matrices, "Quit", iPos[0], buttonWallY + iPos[1], -1);

        setStart(0, 0);

        textRenderer.draw(matrices, "Ballsack Client (" + BallSack.getInstance().man.commitId + "/" + BallSack.getInstance().man.branch + ")", 5, height - 10, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;
        if(button == 0 && (System.currentTimeMillis() > endAnim || !CustomLoadingOverlay.isDoingAnimation)) {
            if(isMouseOn(width / 2 - 100, height / 2 - 55, width / 2 + 100, height / 2 - 35)) {
                this.client.openScreen(new SelectWorldScreen(this));
            }

            if(isMouseOn(width / 2 - 100, height / 2 - 25, width / 2 + 100, height / 2 - 5)) {
                this.client.openScreen(new MultiplayerScreen(this));
            }

            if(isMouseOn(width / 2 - 100, height / 2 + 5, width / 2 + 100, height / 2 + 25)) {
                this.client.openScreen(new HUDMoveUI());
            }

            if(isMouseOn(width / 2 - 100, height / 2 + 35, width / 2 - 5, height / 2 + 55)) {
                this.client.openScreen(new OptionsScreen(this, this.client.options));
            }

            if(isMouseOn(width / 2 + 5, height / 2 + 35, width / 2 + 100, height / 2 + 55)) {
                this.client.scheduleStop();
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isMouseOn(double x, double y, double endX, double endY) {
        return UIUtil.basicCollisionCheck(
                lastMouseX, lastMouseY,
                x, y,
                endX, endY
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}

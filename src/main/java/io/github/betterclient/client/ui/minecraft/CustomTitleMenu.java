package io.github.betterclient.client.ui.minecraft;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.bridge.IBridge.*;
import io.github.betterclient.client.ui.clickgui.HUDMoveUI;
import io.github.betterclient.client.util.UIUtil;
import io.github.betterclient.client.util.autoupdater.AutoUpdaterScreen;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

import static io.github.betterclient.client.util.UIUtil.*;

public class CustomTitleMenu extends Screen {
    public boolean isFirstLaunch;
    public long startAnim, endAnim;


    private int lastMouseX, lastMouseY;
    private float animatedMouseX, animatedMouseY;

    /**
     * choose a background image from the folder
     * background images are AI generated
     */
    public final Identifier chosenBackground;
    private final int bgcolor = new Color(0, 0, 0, 120).getRGB();

    public CustomTitleMenu(boolean firstLaunch) {
        super();
        this.isFirstLaunch = firstLaunch;

        int backgroundNumber = new Random().nextInt(2);
        this.chosenBackground = new Identifier("textures/ballsack/backgrounds/background" + backgroundNumber + ".png");
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
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if(startAnim == 0) {
            startAnim = System.currentTimeMillis();

            endAnim = System.currentTimeMillis() + 2000; //2 second animation
        }

        if(BallSack.getInstance().doUpdate && BallSack.getInstance().man.checkUpdate())
            MinecraftClient.getInstance().setGuiScreen(new AutoUpdaterScreen(chosenBackground, this));
        else
            BallSack.getInstance().doUpdate = false;

        lastMouseX = mouseX;
        lastMouseY = mouseY;
        fill(matrices, 0, 0, width, height, Color.black.getRGB());

        int prepanY = (int) map(animatedMouseY, 0, height, -100, 0);

        int panoramaY;
        int buttonWallY = 0;
        if(CustomLoadingOverlay.isDoingAnimation && this.isFirstLaunch && System.currentTimeMillis() < (endAnim + 250)) { //continue the animation for extra time so its smoother
            CustomLoadingOverlay.doRender = false;

            int alpha = (int) map(System.currentTimeMillis() - startAnim, 0, 2000, 0, 120);
            fill(matrices, 0, 0, width, height, new Color(255, 255, 255, Math.max(120 - alpha, 0)).getRGB());

            panoramaY = (int) map(System.currentTimeMillis() - startAnim, 0, 2000, width, prepanY + 15);
            buttonWallY = (int) map(System.currentTimeMillis() - startAnim, 0, 2000, width, 0);
            if(System.currentTimeMillis() >= endAnim) {
                panoramaY = prepanY;
                buttonWallY = 0;

                //run in separate thread to not stop app
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

        MinecraftClient.getInstance().setShaderTexture(0, chosenBackground);
        MinecraftClient.getInstance().setShaderColor(1, 1, 1, 0.7f);
        drawTexture(matrices, panX, panY, 0, 0, width + 100, height + 100, width + 100, height + 100);

        animatedMouseX += ((mouseX-animatedMouseX) / 1.8f) + 0.1f;
        animatedMouseY += ((mouseY-animatedMouseY) / 1.8f) + 0.1f;

        //a wall of buttons

        setStart(0, buttonWallY);

        drawRoundedRect(width / 2f - 100, height / 2f - 55, width / 2f + 100, height / 2f - 35, 2f, bgcolor);
        float[] iPos = getIdealRenderingPosForText("Singleplayer", width / 2f - 100, height / 2f - 55, width / 2f + 100, height / 2f - 35);
        textRenderer.draw(matrices, "Singleplayer", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2f - 100, height / 2f - 25, width / 2f + 100, height / 2f - 5, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Multiplayer", width / 2f - 100, height / 2f - 25, width / 2f + 100, height / 2f - 5);
        textRenderer.draw(matrices, "Multiplayer", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2f - 100, height / 2f + 5, width / 2f + 100, height / 2f + 25, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Ballsack Settings", width / 2f - 100, height / 2f + 5, width / 2f + 100, height / 2f + 25);
        textRenderer.draw(matrices, "Ballsack Settings", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2f - 100, height / 2f + 35, width / 2f - 5, height / 2f + 55, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Options", width / 2f - 100, height / 2f + 35, width / 2f - 5, height / 2f + 55);
        textRenderer.draw(matrices, "Options", iPos[0], buttonWallY + iPos[1], -1);

        drawRoundedRect(width / 2f + 5, height / 2f + 35, width / 2f + 100, height / 2f + 55, 2f, bgcolor);
        iPos = getIdealRenderingPosForText("Quit", width / 2f + 5, height / 2f + 35, width / 2f + 100, height / 2f + 55);
        textRenderer.draw(matrices, "Quit", iPos[0], buttonWallY + iPos[1], -1);

        CustomModButtons.render(matrices);

        setStart(0, 0);

        textRenderer.draw(matrices, "Ballsack Client (" + BallSack.getInstance().man.commitId + "/" + BallSack.getInstance().man.branch + ")", 5, height - 10, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        lastMouseX = (int) mouseX;
        lastMouseY = (int) mouseY;
        MinecraftClient client = MinecraftClient.getInstance();
        if(button == 0 && (System.currentTimeMillis() > endAnim || !CustomLoadingOverlay.isDoingAnimation)) {
            CustomModButtons.mouseClicked(mouseX, mouseY);
            if(isMouseOn(width / 2f - 100, height / 2f - 55, width / 2f + 100, height / 2f - 35)) {
                client.openNonCustomScreen(NonCustomScreen.SINGLEPLAYER);
            }

            if(isMouseOn(width / 2f - 100, height / 2f - 25, width / 2f + 100, height / 2f - 5)) {
                client.openNonCustomScreen(NonCustomScreen.MULTIPLAYER);
            }

            if(isMouseOn(width / 2f - 100, height / 2f + 5, width / 2f + 100, height / 2f + 25)) {
                client.setGuiScreen(new HUDMoveUI());
            }

            if(isMouseOn(width / 2f - 100, height / 2f + 35, width / 2f - 5, height / 2f + 55)) {
                client.openNonCustomScreen(NonCustomScreen.OPTIONS);
            }

            if(isMouseOn(width / 2f + 5, height / 2f + 35, width / 2f + 100, height / 2f + 55)) {
                client.bs$scheduleStop();
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

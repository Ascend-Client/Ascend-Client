package io.github.betterclient.client.mod.impl.other;

import io.github.betterclient.client.mod.Category;
import io.github.betterclient.client.mod.Module;
import io.github.betterclient.client.mod.setting.ModeSetting;
import io.github.betterclient.client.util.smooth.FontGlyphInfo;
import io.github.betterclient.client.util.smooth.SmoothTextRenderer;

import java.io.IOException;

public class SmoothFont extends Module {
    public static SmoothFont instance = new SmoothFont();
    public ModeSetting font = new ModeSetting("Font", "C059", "C059", "Dialog", "FreeSans", "P052", "Arial") {
        @Override
        public void toggle() {
            super.toggle();
            try {
                SmoothTextRenderer.instance = new SmoothTextRenderer(new FontGlyphInfo(font.value));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private SmoothFont() {
        super("Smooth Font", Category.OTHER, null);
        this.addSetting(font);
    }
}

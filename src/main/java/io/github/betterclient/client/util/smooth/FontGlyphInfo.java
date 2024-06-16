package io.github.betterclient.client.util.smooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FontGlyphInfo {
    public List<FontCharacter> chars = new ArrayList<>();

    public FontGlyphInfo(String fontName) throws IOException {
        this(Objects.requireNonNull(FontGlyphInfo.class.getResourceAsStream("/ballsack/fonts/" + fontName + ".fnt")));
    }

    public FontGlyphInfo(InputStream is) throws IOException {
        String[] lines = new String(is.readAllBytes()).split("\n");
        is.close();

        List<String> gLines = new ArrayList<>();
        boolean isInLines = false;
        for (String line : lines) {
            if(isInLines)
                gLines.add(line);

            if(line.contains("chars count="))
                isInLines = true;
        }

        this.loadGlyphInfo(gLines);
    }

    private void loadGlyphInfo(List<String> data) {
        for (String info : data) {
            while (info.contains("  "))
                info = info.replaceAll(" {2}", " "); //remove whitespace
            String[] words = info.split(" ");

            int id = 0;
            int x = 0;
            int y = 0;
            int width = 0;
            int height = 0;
            int xoff = 0;
            int yoff = 0;
            int xadd = 0;
            for (String word : words) {
                if(word.startsWith("id=")) id = Integer.parseInt(word.replace("id=", ""));
                if(word.startsWith("x=")) x = Integer.parseInt(word.replace("x=", ""));
                if(word.startsWith("y=")) y = Integer.parseInt(word.replace("y=", ""));
                if(word.startsWith("width=")) width = Integer.parseInt(word.replace("width=", ""));
                if(word.startsWith("height=")) height = Integer.parseInt(word.replace("height=", ""));
                if(word.startsWith("xoffset=")) xoff = Integer.parseInt(word.replace("xoffset=", ""));
                if(word.startsWith("yoffset=")) yoff = Integer.parseInt(word.replace("yoffset=", ""));
                if(word.startsWith("xadvance=")) xadd = Integer.parseInt(word.replace("xadvance=", ""));
            }
            int orW = width;
            int orH = height;

            if(width == 0) width++;
            if(xadd == 0) xadd++;
            if(height == 0) height++;

            if(id == 32) xadd = 8;

            this.chars.add(new FontCharacter(
                    (char) id, x, y, width, height, xoff, yoff, xadd, orW, orH
            ));
        }
    }

    public boolean containsChar(int i) {
        return this.chars.stream().anyMatch(fontCharacter -> fontCharacter.id() == i);
    }

    public FontCharacter getChar(int i) {
        return this.chars.stream().filter(fontCharacter -> fontCharacter.id() == i).toList().get(0);
    }
}

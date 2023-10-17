
package com.jtorleonstudios.libraryferret.gui;

import java.util.regex.Pattern;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

public class AbstractScreen extends Screen {
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
    public static final int PADDING = 6;

    protected AbstractScreen(Text screenTitle) {
        super(screenTitle);
    }

    public TextRenderer getFontRenderer() {
        return super.textRenderer;
    }

    public int getLineHeight() {
        return this.getFontRenderer().fontHeight;
    }

    public Window getWindow() {
        return this.client.getWindow();
    }

    public TextureManager getTextureManager() {
        return this.client.getTextureManager();
    }

    public static String stripColor(String p_76338_0_) {
        return STRIP_COLOR_PATTERN.matcher(p_76338_0_).replaceAll("");
    }
}


package com.jtorleonstudios.libraryferret.gui;

import com.jtorleonstudios.libraryferret.utils.Color;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

public abstract class AbstractUI extends AbstractParentElement implements Drawable {
    public final AbstractScreen parent;
    public int x;
    public int y;
    public int width;
    public int height;
    public int left;
    public int right;
    public int top;
    public int bottom;

    public AbstractUI(AbstractScreen parent, int x, int y, int width, int height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.left = this.x;
        this.top = this.y;
        this.right = this.x + this.width;
        this.bottom = this.y + this.height;
    }

    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    public final boolean testAABB(double mouseX, double mouseY) {
        return mouseX >= (double)this.left && mouseX <= (double)this.right && mouseY >= (double)this.top && mouseY <= (double)this.bottom;
    }

    public void tick() {
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.testAABB(mouseX, mouseY);
    }

    @SuppressWarnings("deprecation")
    public final void renderBackground() {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        RenderSystem.disableLighting();
        RenderSystem.disableFog();
        this.parent.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        b.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        b.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0F, (float)this.bottom / 32.0F).color(32, 32, 32, 255).next();
        b.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0F, (float)this.bottom / 32.0F).color(32, 32, 32, 255).next();
        b.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0F, (float)this.top / 32.0F).color(32, 32, 32, 255).next();
        b.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0F, (float)this.top / 32.0F).color(32, 32, 32, 255).next();
        t.draw();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    public final void renderTexts(List<String> texts, MatrixStack matrix, int x, int y, int ignoredColor) {
        float ry = (float)y;
        for(Iterator<String> var7 = texts.iterator(); var7.hasNext(); ry += (float)this.parent.getLineHeight()) {
            String v = var7.next();
            this.parent.getFontRenderer().drawWithShadow(matrix, v, (float)x, ry, 16777215);
        }
    }

    public final List<String> resizeTextToWidth(String text) {
        List<StringVisitable> data = this.parent.getFontRenderer().getTextHandler().wrapLines(text, this.width - this.width / 8, Style.EMPTY);
        List<String> lines = new ArrayList<>();
        for (StringVisitable v : data)
            lines.add(v.getString().replaceAll("\n", "").replaceAll("\r", ""));
        return lines;
    }

    public final void fillDebug(MatrixStack matrix) {
        fill(matrix, this.left, this.top, this.right, this.bottom, Color.RED);
    }
}

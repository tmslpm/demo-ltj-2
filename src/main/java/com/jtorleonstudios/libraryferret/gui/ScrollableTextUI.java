
package com.jtorleonstudios.libraryferret.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public class ScrollableTextUI extends AbstractUI {
    private List<String> texts;
    private final int barWidth = 6;
    private final int barLeft;
    private boolean scrolling;
    private double scrollDistance;

    public ScrollableTextUI(AbstractScreen parent, String text, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.barLeft = this.right - 6;
        this.texts = this.resizeTextToWidth(text);
        this.scrolling = false;
        this.scrollDistance = 0.0;
    }

    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        double scale = this.parent.getWindow().getScaleFactor();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder world = tess.getBuffer();
        GL11.glEnable(3089);
        GL11.glScissor((int)((double)this.left * scale), (int)((double)this.parent.getWindow().getHeight() - (double)this.bottom * scale), (int)((double)this.width * scale), (int)((double)this.height * scale));
        this.renderTexts(this.texts, matrix, this.left + this.getInnerPadding(), this.getInnerPadding() + (int)((double)this.y - this.scrollDistance), 16777215);
        if (this.requireScroll()) {
            int barHeight = this.getBarHeight();
            int extraHeight = this.getContentHeight() - this.height;
            int barTop = (int)this.scrollDistance * (this.height - barHeight) / extraHeight + this.top;
            if (barTop < this.top) {
                barTop = this.top;
            }

            RenderSystem.disableTexture();
            world.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            world.vertex(this.barLeft, this.bottom, 0.0).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
            world.vertex(this.barLeft + 6, this.bottom, 0.0).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
            world.vertex(this.barLeft + 6, this.top, 0.0).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
            world.vertex(this.barLeft, this.top, 0.0).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
            tess.draw();
            world.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            world.vertex(this.barLeft, barTop + barHeight, 0.0).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
            world.vertex(this.barLeft + 6, barTop + barHeight, 0.0).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
            world.vertex(this.barLeft + 6, barTop, 0.0).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
            world.vertex(this.barLeft, barTop, 0.0).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
            tess.draw();
            world.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            world.vertex(this.barLeft, barTop + barHeight - 1, 0.0).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
            world.vertex(this.barLeft + 6 - 1, barTop + barHeight - 1, 0.0).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
            world.vertex(this.barLeft + 6 - 1, barTop, 0.0).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
            world.vertex(this.barLeft, barTop, 0.0).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
            tess.draw();
            RenderSystem.enableTexture();
        }

        GL11.glDisable(3089);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (!super.mouseScrolled(mouseX, mouseY, scroll) && scroll != 0.0 && this.requireScroll()) {
            this.scrollDistance += -scroll * 20.0;
            this.applyScrollLimits();
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return false;
        } else {
            this.scrolling = this.requireScroll() && button == 0 && mouseX >= (double)this.barLeft && mouseX < (double)(this.barLeft + 6);
            return this.scrolling;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return false;
        } else {
            this.scrolling = !this.scrolling;
            return !this.scrolling;
        }
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) && this.scrolling) {
            int maxScroll = this.height - this.getBarHeight();
            double moved = deltaY / (double)maxScroll;
            this.scrollDistance += (double)(this.getContentHeight() - this.height) * moved;
            this.applyScrollLimits();
            return true;
        } else {
            return false;
        }
    }

    private void applyScrollLimits() {
        int max = this.getContentHeight() - this.height;
        if (max < 0) {
            max /= 2;
        }

        this.scrollDistance = this.scrollDistance < 0.0 ? 0.0 : (Math.min(this.scrollDistance, (double) max));
    }

    public void setTexts(String text) {
        this.texts = this.resizeTextToWidth(text);
    }

    private int getContentHeight() {
        int h = this.texts.size() * this.parent.getLineHeight() + this.getInnerPadding() * 2;
        if (h < this.bottom - this.top - 8) {
            h = this.bottom - this.top - 8;
        }

        return h;
    }

    private int getBarHeight() {
        int barHeight = this.height * this.height / this.getContentHeight();
        if (barHeight < 32) {
            barHeight = 32;
        }

        if (barHeight > this.height * 2) {
            barHeight = this.height * 2;
        }

        return barHeight;
    }

    private boolean requireScroll() {
        return this.getContentHeight() > this.height;
    }

    private int getInnerPadding() {
        return 6;
    }
}

package earth.terrarium.adastra.client.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class LabeledImageButton extends ImageButton {

    public LabeledImageButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(x, y, width, height, sprites, onPress);
    }

    public LabeledImageButton(int x, int y, int width, int height, WidgetSprites sprites, OnPress onPress, Component message) {
        super(x, y, width, height, sprites, onPress, message);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Inactive buttons are tinted grey (was RenderSystem.setShaderColor(0.5, 0.5, 0.5, 1)).
        Identifier sprite = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height, isActive() ? -1 : ARGB.colorFromFloat(1, 0.5f, 0.5f, 0.5f));
        this.handleCursor(graphics);

        int color = active ? 0xffffff : 0xa0a0a0;
        graphics.centeredText(Minecraft.getInstance().font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, color | Mth.ceil(alpha * 255.0F) << 24);
    }
}

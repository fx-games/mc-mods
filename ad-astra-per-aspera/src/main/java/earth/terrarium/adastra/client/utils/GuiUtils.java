package earth.terrarium.adastra.client.utils;

import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.adastra.common.storage.ResourceStack;
import earth.terrarium.adastra.common.storage.FluidResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class GuiUtils {

    public static final Identifier ENERGY_BAR = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "energy_bar");
    public static final int ENERGY_BAR_WIDTH = 13;
    public static final int ENERGY_BAR_HEIGHT = 46;

    public static final Identifier FLUID_BAR = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "fluid_bar");
    public static final int FLUID_BAR_WIDTH = 12;
    public static final int FLUID_BAR_HEIGHT = 46;

    public static final Identifier HAMMER = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/gui/sprites/hammer.png");
    public static final Identifier SNOWFLAKE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/gui/sprites/snowflake.png");
    public static final Identifier FIRE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/gui/sprites/fire.png");
    public static final Identifier ARROW = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/gui/sprites/arrow.png");
    public static final Identifier SUN = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "sun");
    public static final Identifier SLIDER = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "slider");

    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = createPressableButtonSprites("settings_button");
    public static final WidgetSprites RESET_BUTTON_SPRITES = createPressableButtonSprites("reset_button");
    public static final WidgetSprites SHOW_BUTTON_SPRITES = createPressableButtonSprites("show_button");
    public static final WidgetSprites HIDE_BUTTON_SPRITES = createPressableButtonSprites("hide_button");

    public static final WidgetSprites CRAFTING_BUTTON_SPRITES = createPressableButtonSprites("crafting_button");
    public static final WidgetSprites FURNACE_BUTTON_SPRITES = createPressableButtonSprites("furnace_button");

    public static final WidgetSprites NONE_BUTTON_SPRITES = createPressableButtonSprites("side_config/none");
    public static final WidgetSprites PUSH_BUTTON_SPRITES = createPressableButtonSprites("side_config/push");
    public static final WidgetSprites PULL_BUTTON_SPRITES = createPressableButtonSprites("side_config/pull");
    public static final WidgetSprites PUSH_PULL_BUTTON_SPRITES = createPressableButtonSprites("side_config/push_pull");

    public static final WidgetSprites REDSTONE_ALWAYS_ON_SPRITES = createPressableButtonSprites("redstone/always_on_button");
    public static final WidgetSprites REDSTONE_ON_WHEN_POWERED_SPRITES = createPressableButtonSprites("redstone/on_when_powered_button");
    public static final WidgetSprites REDSTONE_ON_WHEN_NOT_POWERED_SPRITES = createPressableButtonSprites("redstone/on_when_not_powered_button");
    public static final WidgetSprites REDSTONE_NEVER_ON_SPRITES = createPressableButtonSprites("redstone/never_on_button");

    public static WidgetSprites createPressableButtonSprites(String name) {
        return new WidgetSprites(
            Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "buttons/" + name),
            Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "buttons/" + name + "_pressed"),
            Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "buttons/" + name + "_highlighted")
        );
    }

    public static void drawEnergyBar(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, long energy, long capacity, Component... tooltips) {
        float ratio = energy / (float) capacity;
        scissor(graphics, x + 6, y - 31 + ENERGY_BAR_HEIGHT - (int) (ENERGY_BAR_HEIGHT * ratio), ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY_BAR, x + 6, y - 31, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);
        graphics.disableScissor();

        drawTooltips(graphics, mouseX, mouseY, x + 6, x + 19, y - 31, y + 15, list -> {
            list.add(TooltipUtils.getEnergyComponent(energy, capacity));
            Collections.addAll(list, tooltips);
            return list;
        });
    }

    public static void drawFluidBar(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, ResourceStack<FluidResource> fluid, long capacity, Component... tooltips) {
        if (!fluid.isEmpty()) {
            float ratio = fluid.amount() / (float) capacity;
            TextureAtlasSprite sprite = ClientPlatformUtils.getFluidSprite(fluid);
            int color = ARGB.opaque(ClientPlatformUtils.getFluidColor(fluid));

            scissor(graphics, x + 6, y - 31 + FLUID_BAR_HEIGHT - (int) (FLUID_BAR_HEIGHT * ratio), FLUID_BAR_WIDTH, FLUID_BAR_HEIGHT);
            for (int i = 1; i < 5; i++) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x + 7, y - 34 + FLUID_BAR_HEIGHT - i * 12, 14, 14, color);
            }
            graphics.disableScissor();
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FLUID_BAR, x + 6, y - 31, FLUID_BAR_WIDTH, FLUID_BAR_HEIGHT);

        drawTooltips(graphics, mouseX, mouseY, x + 6, x + 18, y - 31, y + 15, list -> {
            list.add(TooltipUtils.getFluidComponent(fluid, capacity));
            Collections.addAll(list, tooltips);
            return list;
        });
    }

    public static void drawHorizontalProgressBar(GuiGraphicsExtractor graphics, Identifier texture, int mouseX, int mouseY, int x, int y, int width, int height, int progress, int maxProgress, boolean reverse, Component... tooltips) {
        int widthProgress = (int) (width * (progress / (float) maxProgress));
        if (reverse) widthProgress = width - widthProgress;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, widthProgress, height, width, height);

        drawTooltips(graphics, mouseX, mouseY, x, x + width, y, y + height, list -> {
            Collections.addAll(list, tooltips);
            return list;
        });
    }

    public static void drawVerticalProgressBar(GuiGraphicsExtractor graphics, Identifier texture, int mouseX, int mouseY, int x, int y, int width, int height, int progress, int maxProgress, Component... tooltips) {
        int heightProgress = (int) (height * (progress / (float) maxProgress));
        heightProgress = height - heightProgress;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y + heightProgress, 0, heightProgress, width, height - heightProgress, width, height);

        drawTooltips(graphics, mouseX, mouseY, x, x + width, y, y + height, list -> {
            Collections.addAll(list, tooltips);
            return list;
        });
    }

    /** Scissor by position and size (the old RenderUtils.createScissorBox shape). Pair with graphics.disableScissor(). */
    public static void scissor(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.enableScissor(x, y, x + width, y + height);
    }

    public static void drawTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int minX, int maxX, int minY, int maxY, Function<List<Component>, List<Component>> tooltips) {
        if (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY) {
            List<Component> lines = tooltips.apply(new ArrayList<>());
            lines.removeIf(c -> c.getString().isEmpty());
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, lines, mouseX, mouseY);
        }
    }

    public static void drawColoredShadowCenteredString(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color, int shadowColor) {
        FormattedCharSequence formattedCharSequence = text.getVisualOrderText();
        graphics.text(font, formattedCharSequence, x - font.width(formattedCharSequence) / 2 - 1, y + 1, ARGB.opaque(shadowColor), false);
        graphics.text(font, formattedCharSequence, x - font.width(formattedCharSequence) / 2, y, ARGB.opaque(color), false);
    }

    public static void drawColoredShadowString(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color, int shadowColor) {
        graphics.text(font, text, x - 1, y + 1, ARGB.opaque(shadowColor), false);
        graphics.text(font, text, x, y, ARGB.opaque(color), false);
    }
}

package earth.terrarium.adastra.client.components.machines;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.client.components.base.TickableWidget;
import earth.terrarium.adastra.client.screens.base.ConfigurationScreen;
import earth.terrarium.adastra.client.utils.GuiUtils;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.menus.configuration.FluidConfiguration;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packets.ServerboundClearFluidTankPacket;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.adastra.common.storage.ResourceStack;
import earth.terrarium.adastra.common.storage.FluidResource;
import earth.terrarium.adastra.common.storage.CommonStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.ARGB;

import java.time.Duration;

public class FluidBarWidget extends ConfigurationWidget implements TickableWidget {

    protected final BlockPos tankPos;
    protected final int tank;
    protected final CommonStorage<FluidResource> container;
    protected long lastFluidAmount;
    protected long difference;

    public FluidBarWidget(BlockPos tankPos, FluidConfiguration configuration) {
        super(configuration, GuiUtils.FLUID_BAR_WIDTH, GuiUtils.FLUID_BAR_HEIGHT);
        this.tankPos = tankPos;
        this.tank = configuration.tank();
        this.container = configuration.container();
    }

    @Override
    public void tick() {
        ResourceStack<FluidResource> holder = this.container.getContents(this.tank);
        this.difference = holder.amount() - this.lastFluidAmount;
        this.lastFluidAmount = holder.amount();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, partialTick);
        ResourceStack<FluidResource> holder = this.container.getContents(this.tank);
        long capacity = this.container.getLimit(this.tank, FluidResource.BLANK);
        long amount = holder.amount();
        float ratio = amount / (float) capacity;
        int x = this.getX();
        int y = this.getY();

        if (!holder.isEmpty()) {
            TextureAtlasSprite sprite = ClientPlatformUtils.getFluidSprite(holder);
            int color = ARGB.opaque(ClientPlatformUtils.getFluidColor(holder));

            GuiUtils.scissor(graphics, x, y + GuiUtils.FLUID_BAR_HEIGHT - (int) (GuiUtils.FLUID_BAR_HEIGHT * ratio), GuiUtils.FLUID_BAR_WIDTH, GuiUtils.FLUID_BAR_HEIGHT);
            for (int i = 1; i < 5; i++) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y + GuiUtils.FLUID_BAR_HEIGHT - i * 16, 16, 16, color);
            }
            graphics.disableScissor();
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, GuiUtils.FLUID_BAR, x, y, GuiUtils.FLUID_BAR_WIDTH, GuiUtils.FLUID_BAR_HEIGHT);
        if (this.isHovered() && Minecraft.getInstance().hasShiftDown()) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }

        if (this.isHoveredOrFocused()) {
            if (holder.isEmpty()) {
                setTooltip(Tooltip.create(CommonComponents.joinLines(
                    TooltipUtils.getFluidComponent(holder, capacity),
                    TooltipUtils.getFluidDifferenceComponent(difference)
                )));
            } else {
                setTooltip(Tooltip.create(CommonComponents.joinLines(
                    TooltipUtils.getFluidComponent(holder, capacity),
                    TooltipUtils.getFluidDifferenceComponent(difference),
                    ConstantComponents.CLEAR_FLUID_TANK
                )));
            }
            setTooltipDelay(Duration.ofMillis(0));
        }
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo button) {
        return super.isValidClickButton(button) || (button.button() == 1 && button.hasShiftDown());
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (ConfigurationScreen.isConfigurable()) {
            super.onClick(event, doubleClick);
        } else {
            NetworkHandler.CHANNEL.sendToServer(new ServerboundClearFluidTankPacket(this.tankPos, this.tank));
        }
    }
}

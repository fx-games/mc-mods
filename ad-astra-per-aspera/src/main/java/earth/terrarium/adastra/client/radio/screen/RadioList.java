package earth.terrarium.adastra.client.radio.screen;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.config.AdAstraConfigClient;
import earth.terrarium.adastra.client.config.RadioConfig;
import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packets.ServerboundSetStationPacket;
import earth.terrarium.adastra.common.utils.radio.StationInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Was ResourcefulLib's SelectionList (removed in RL 5); now a vanilla ObjectSelectionList without background/scrollbar.
public class RadioList extends ObjectSelectionList<RadioList.RadioEntry> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "textures/radio/ui.png");
    private static final int WIDTH = 91;

    @Nullable
    private final BlockPos pos;
    private List<StationInfo> stations = new ArrayList<>();
    private String playing = null;

    public RadioList(int x, int y, @Nullable BlockPos pos) {
        super(Minecraft.getInstance(), WIDTH, 41, y, 12);
        this.setX(x);
        this.centerListVertically = false;
        this.pos = pos;
    }

    @Override
    public void setSelected(@Nullable RadioEntry entry) {
        super.setSelected(entry);
        if (entry != null) {
            StationInfo info = entry.info;
            if (info == null) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundSetStationPacket("", pos));
            } else if (!Objects.equals(RadioHandler.getPlaying(), info.url())) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundSetStationPacket(info.url(), pos));
            }
        }
    }

    @Override
    public int getRowWidth() {
        return WIDTH;
    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor graphics) {}

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor graphics) {}

    @Override
    protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}

    @Override
    protected void extractSelection(GuiGraphicsExtractor graphics, RadioEntry entry, int outlineColor) {}

    public void update(List<StationInfo> stations, String url) {
        this.stations = stations;
        this.playing = url;
        update();
    }

    private void update() {
        RadioEntry selected = null;
        List<RadioEntry> entries = new ArrayList<>();
        Set<String> favoriteUrls = Sets.newHashSet(RadioConfig.favorites);
        List<StationInfo> favorites = new ArrayList<>();
        List<StationInfo> others = new ArrayList<>();

        for (StationInfo station : this.stations) {
            if (favoriteUrls.contains(station.url())) {
                favorites.add(station);
            } else {
                others.add(station);
            }
        }
        favorites.sort(Comparator.comparing(StationInfo::title));
        others.sort(Comparator.comparing(StationInfo::title));

        entries.add(new RadioEntry(null));

        for (StationInfo favorite : favorites) {
            RadioEntry entry = new RadioEntry(favorite);
            if (favorite.url().equals(this.playing)) selected = entry;
            entry.favorite = true;
            entries.add(entry);
        }

        for (StationInfo other : others) {
            RadioEntry entry = new RadioEntry(other);
            if (other.url().equals(this.playing)) selected = entry;
            entries.add(entry);
        }

        replaceEntries(entries);
        if (selected != null) {
            setSelected(selected);
        }
    }

    public class RadioEntry extends ObjectSelectionList.Entry<RadioEntry> {

        @Nullable
        private final StationInfo info;
        private boolean favorite = false;

        public RadioEntry(@Nullable StationInfo info) {
            this.info = info;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
            Font font = Minecraft.getInstance().font;
            int left = getX();
            int top = getY();
            boolean selected = RadioList.this.getSelected() == this;
            int v = selected ? 66 : hovered ? 42 : 54;
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left + 1, top, 253, v, 89, 12, 512, 256);
            int textStart = left + 3;
            if ((favorite || hovered) && this.info != null) {
                String text = favorite ? "★" : "☆";
                graphics.text(font, text, textStart, top + 3, favorite ? 0xFFFFAA00 : 0xFFFFFFFF);
                textStart += font.width(text) + 1 + 2;
            }
            graphics.text(font, getName(), textStart, top + 3, 0xFFFFFFFF);

            if (hovered) {
                if (info != null) {
                    graphics.setTooltipForNextFrame(Component.literal(info.name()), mouseX, mouseY);
                }
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            double mouseX = event.x() - getX();
            double mouseY = event.y() - getY();
            int width = Minecraft.getInstance().font.width(favorite ? "★" : "☆");
            if (info != null && mouseX > 0 && mouseX < width + 3 && mouseY > 0 && mouseY < 12) {
                List<String> favorites = new ArrayList<>(Arrays.asList(RadioConfig.favorites));
                if (favorite) {
                    favorites.remove(info.url());
                } else {
                    favorites.add(info.url());
                }
                RadioConfig.favorites = favorites.toArray(new String[0]);
                AdAstra.CONFIGURATOR.saveConfig(AdAstraConfigClient.class);
                update();
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }

        private Component getName() {
            return info == null ? Component.translatable("text.ad_astra.radio.none") : Component.literal(info.title());
        }

        @Override
        public Component getNarration() {
            return getName();
        }
    }
}

package dev.explorercraft.starlight;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.explorercraft.starlight.client.SpyglassZoom;
import dev.explorercraft.starlight.client.StarField;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class StarlightClient implements ClientModInitializer {
    public static final String MOD_ID = "starlight";
    /// How far off the crosshair a figure's nearest star may be and still get named.
    private static final double LABEL_RADIUS_DEGREES = 8;

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath(MOD_ID, "constellation_label"), StarlightClient::renderLabel);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommands.literal(MOD_ID)
                        .executes(ctx -> status(ctx.getSource()))
                        .then(ClientCommands.literal("status").executes(ctx -> status(ctx.getSource())))
                        .then(ClientCommands.literal("date")
                                .then(ClientCommands.argument("date", StringArgumentType.word())
                                        .executes(ctx -> pinDate(ctx.getSource(), StringArgumentType.getString(ctx, "date")))))));
    }

    private static int status(FabricClientCommandSource source) {
        double sidereal = StarField.lastSidereal;
        String polaris = SkyCatalog.INSTANCE.stars.stream()
                .filter(s -> s.kind() == SkyCatalog.Kind.POLARIS)
                .map(s -> String.format(Locale.ROOT, "%.1f°", SkyClock.altitude(s, sidereal)))
                .findFirst().orElse("missing");
        source.sendFeedback(Component.literal(String.format(Locale.ROOT,
                "Starlight: %s%s, sidereal time %02d:%02d, Polaris at %s, star brightness %.2f, spyglass overlay %.2f, looking at %s",
                SkyClock.date(), SkyClock.isPinned() ? " (pinned)" : "",
                (int) (sidereal / 15), (int) (sidereal * 4 % 60), polaris,
                StarField.lastStarBrightness, StarField.overlay,
                lookedAt() instanceof SkyCatalog.Figure f ? f.abbr() : "no constellation")));
        return 1;
    }

    private static int pinDate(FabricClientCommandSource source, String text) {
        if (text.equals("today")) {
            SkyClock.pinDate(null);
            source.sendFeedback(Component.literal("Starlight follows the real calendar again"));
            return 1;
        }
        try {
            SkyClock.pinDate(LocalDate.parse(text));
            source.sendFeedback(Component.literal("Starlight sky pinned to " + text));
            return 1;
        } catch (DateTimeParseException e) {
            source.sendError(Component.literal("Expected a date like 2026-12-24, or today"));
            return 0;
        }
    }

    /// The constellation under the crosshair, by the nearest of its stars.
    private static SkyCatalog.Figure lookedAt() {
        var player = Minecraft.getInstance().player;
        if (player == null) return null;
        var look = player.getViewVector(1f);
        Vector3f eq = new Matrix4f(StarField.lastEquatorialToWorld).transpose()
                .transformDirection(new Vector3f((float) look.x, (float) look.y, (float) look.z));

        var sky = SkyCatalog.INSTANCE;
        double limit = Math.cos(Math.toRadians(LABEL_RADIUS_DEGREES));
        SkyCatalog.Figure best = null;
        double bestDot = limit;
        for (var figure : sky.figures) {
            for (int index : figure.lines()) {
                float[] d = sky.stars.get(index).direction();
                double dot = d[0] * eq.x + d[1] * eq.y + d[2] * eq.z;
                if (dot > bestDot) {
                    bestDot = dot;
                    best = figure;
                }
            }
        }
        return best;
    }

    private static void renderLabel(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        var minecraft = Minecraft.getInstance();
        renderZoom(graphics, minecraft);
        float alpha = StarField.overlay * Math.min(1f, StarField.lastStarBrightness * 2f);
        if (alpha < 0.05f) return;
        var figure = lookedAt();
        if (figure == null) return;
        int x = graphics.guiWidth() / 2, y = graphics.guiHeight() / 2 + 24;
        graphics.centeredText(minecraft.font, Component.translatable(figure.translationKey()), x, y,
                ARGB.color(Math.round(alpha * 255), 0xC8DCFF));
    }

    /// The magnification for a moment after the scroll wheel changed it.
    private static void renderZoom(GuiGraphicsExtractor graphics, Minecraft minecraft) {
        long age = System.currentTimeMillis() - SpyglassZoom.changedAtMillis;
        if (age > 1500 || minecraft.player == null || !minecraft.player.isScoping()) return;
        float alpha = Math.min(1f, (1500 - age) / 500f);
        graphics.centeredText(minecraft.font, Component.literal(SpyglassZoom.magnification() + "×"),
                graphics.guiWidth() / 2, graphics.guiHeight() / 2 + 38, ARGB.color(Math.round(alpha * 255), 0xFFFFFF));
    }
}

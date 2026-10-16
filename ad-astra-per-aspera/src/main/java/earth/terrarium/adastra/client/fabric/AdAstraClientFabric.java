package earth.terrarium.adastra.client.fabric;

import earth.terrarium.adastra.client.AdAstraClient;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.server.packs.PackType;

public class AdAstraClientFabric {

    public static void init() {
        AdAstraClient.init();
        AdAstraClient.onAddReloadListener(ResourceLoader.get(PackType.CLIENT_RESOURCES)::registerReloadListener);
        ClientTickEvents.START_CLIENT_TICK.register(AdAstraClient::clientTick);
        KeyMappingHelper.registerKeyMapping(AdAstraClient.KEY_TOGGLE_SUIT_FLIGHT);
        KeyMappingHelper.registerKeyMapping(AdAstraClient.KEY_OPEN_RADIO);
        AdAstraClient.onRegisterParticles((particle, provider) -> ParticleProviderRegistry.getInstance().register(particle, provider::create));
        AdAstraClient.onRegisterSpecialRenderers(SpecialModelRenderers.ID_MAPPER::put);
        AdAstraClient.onRegisterEntityLayers((location, definition) -> ModelLayerRegistry.registerModelLayer(location, definition::get));
        AdAstraClient.onRegisterHud(HudElementRegistry::addLast);
        ModelLoadingPlugin.register(ctx -> AdAstraClient.onRegisterModels(id -> ClientPlatformUtils.registerExtraModel(ctx, id)));
        LevelRenderEvents.COLLECT_SUBMITS.register(ctx -> AdAstraClient.renderOverlays(ctx.poseStack(), ctx.submitNodeCollector(), ctx.levelState().cameraRenderState.pos));

        AdAstraClient.registerScreens(MenuScreens::register);
        AdAstraClient.registerBlockEntityRenderers(BlockEntityRendererRegistry::register);
        AdAstraClient.registerEntityRenderers(EntityRendererRegistry::register);
    }
}

package earth.terrarium.adastra.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.client.config.AdAstraConfigClient;
import earth.terrarium.adastra.client.dimension.AdAstraPlanetRenderers;
import earth.terrarium.adastra.client.models.armor.SpaceSuitModel;
import earth.terrarium.adastra.client.models.entities.mobs.*;
import earth.terrarium.adastra.client.models.entities.vehicles.LanderModel;
import earth.terrarium.adastra.client.models.entities.vehicles.RocketModel;
import earth.terrarium.adastra.client.models.entities.vehicles.RoverModel;
import earth.terrarium.adastra.client.particle.LargeFlameParticle;
import earth.terrarium.adastra.client.particle.OxygenBubbleParticle;
import earth.terrarium.adastra.client.radio.audio.RadioHandler;
import earth.terrarium.adastra.client.renderers.blocks.*;
import earth.terrarium.adastra.client.renderers.entities.mobs.*;
import earth.terrarium.adastra.client.renderers.entities.vehicles.LanderRenderer;
import earth.terrarium.adastra.client.renderers.entities.vehicles.RocketRenderer;
import earth.terrarium.adastra.client.renderers.entities.vehicles.RoverRenderer;
import earth.terrarium.adastra.client.renderers.world.OverlayRenderer;
import earth.terrarium.adastra.client.screens.PlanetsScreen;
import earth.terrarium.adastra.client.screens.machines.*;
import earth.terrarium.adastra.client.screens.player.OverlayScreen;
import earth.terrarium.adastra.client.screens.vehicles.LanderScreen;
import earth.terrarium.adastra.client.screens.vehicles.RocketScreen;
import earth.terrarium.adastra.client.screens.vehicles.RoverScreen;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packets.ServerboundSyncKeybindPacket;
import earth.terrarium.adastra.common.registry.*;
import earth.terrarium.adastra.common.tags.ModItemTags;
import earth.terrarium.adastra.common.utils.KeybindManager;
import earth.terrarium.adastra.common.utils.radio.RadioHolder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.player.LocalPlayer;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdAstraClient {

    /** Set on humanoid render states when an item tagged held_over_head is held. */
    public static final net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey<Integer> HELD_OVER_HEAD = net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey.create(() -> "ad_astra:held_over_head");

    public static final OverlayRenderer OXYGEN_OVERLAY_RENDERER = new OverlayRenderer(0x4099ccff, () -> AdAstraConfigClient.showOxygenDistributorArea, ModBlocks.OXYGEN_DISTRIBUTOR);
    public static final OverlayRenderer GRAVITY_OVERLAY_RENDERER = new OverlayRenderer(0x40DE2F14, () -> AdAstraConfigClient.showGravityNormalizerArea, ModBlocks.GRAVITY_NORMALIZER);

    // Translation key of the category is now "key.category.ad_astra.main".
    public static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "main"));

    public static final KeyMapping KEY_TOGGLE_SUIT_FLIGHT = new KeyMapping(
        "key.ad_astra.toggle_suit_flight",
        InputConstants.KEY_V,
        KEY_CATEGORY);

    public static final KeyMapping KEY_OPEN_RADIO = new KeyMapping(
        "key.ad_astra.open_radio",
        InputConstants.KEY_R,
        KEY_CATEGORY);

    public static void init() {
        ModClientFluids.CLIENT_FLUIDS.init();
        AdAstra.CONFIGURATOR.register(AdAstraConfigClient.class);
        registerArmor();

        AdAstra.setRegistryAccess(() -> {
            var connection = Minecraft.getInstance().getConnection();
            if (connection == null) return RegistryAccess.EMPTY;
            return connection.registryAccess();
        });
    }

    public static void registerScreens(ScreenRegistrar registrar) {
        registrar.register(ModMenus.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
        registrar.register(ModMenus.COMPRESSOR.get(), CompressorScreen::new);
        registrar.register(ModMenus.ETRIONIC_BLAST_FURNACE.get(), EtrionicBlastFurnaceScreen::new);
        registrar.register(ModMenus.OXYGEN_LOADER.get(), OxygenLoaderScreen::new);
        registrar.register(ModMenus.FUEL_REFINERY.get(), FuelRefineryScreen::new);
        registrar.register(ModMenus.WATER_PUMP.get(), WaterPumpScreen::new);
        registrar.register(ModMenus.SOLAR_PANEL.get(), SolarPanelScreen::new);
        registrar.register(ModMenus.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorScreen::new);
        registrar.register(ModMenus.GRAVITY_NORMALIZER.get(), GravityNormalizerScreen::new);
        registrar.register(ModMenus.CRYO_FREEZER.get(), CryoFreezerScreen::new);
        registrar.register(ModMenus.NASA_WORKBENCH.get(), NasaWorkbenchScreen::new);

        registrar.register(ModMenus.ROCKET.get(), RocketScreen::new);
        registrar.register(ModMenus.ROVER.get(), RoverScreen::new);
        registrar.register(ModMenus.LANDER.get(), LanderScreen::new);

        registrar.register(ModMenus.PLANETS.get(), PlanetsScreen::new);
    }

    public interface ScreenRegistrar {
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
            MenuType<? extends M> type, MenuScreens.ScreenConstructor<M, U> factory
        );
    }

    public static void registerBlockEntityRenderers(BlockEntityRegistrar registrar) {
        registrar.register(ModBlockEntityTypes.ENERGIZER.get(), EnergizerBlockEntityRenderer::new);
        registrar.register(ModBlockEntityTypes.GLOBE.get(), GlobeBlockEntityRenderer::new);
        registrar.register(ModBlockEntityTypes.OXYGEN_DISTRIBUTOR.get(), OxygenDistributorBlockEntityRenderer::new);
        registrar.register(ModBlockEntityTypes.GRAVITY_NORMALIZER.get(), GravityNormalizerBlockEntityRenderer::new);
        registrar.register(ModBlockEntityTypes.FLAG.get(), FlagBlockEntityRenderer::new);
        registrar.register(ModBlockEntityTypes.SLIDING_DOOR.get(), SlidingDoorBlockEntityRenderer::new);
    }

    public interface BlockEntityRegistrar {
        <T extends BlockEntity, S extends BlockEntityRenderState> void register(
            BlockEntityType<T> type, BlockEntityRendererProvider<T, S> factory
        );
    }

    public static void registerEntityRenderers(EntityRegistrar registrar) {
        registrar.register(ModEntityTypes.AIR_VORTEX.get(), NoopRenderer::new);
        registrar.register(ModEntityTypes.ROVER.get(), RoverRenderer::new);
        registrar.register(ModEntityTypes.TIER_1_ROCKET.get(), c -> new RocketRenderer(c, RocketModel.TIER_1_LAYER, RocketRenderer.TIER_1_TEXTURE));
        registrar.register(ModEntityTypes.TIER_2_ROCKET.get(), c -> new RocketRenderer(c, RocketModel.TIER_2_LAYER, RocketRenderer.TIER_2_TEXTURE));
        registrar.register(ModEntityTypes.TIER_3_ROCKET.get(), c -> new RocketRenderer(c, RocketModel.TIER_3_LAYER, RocketRenderer.TIER_3_TEXTURE));
        registrar.register(ModEntityTypes.TIER_4_ROCKET.get(), c -> new RocketRenderer(c, RocketModel.TIER_4_LAYER, RocketRenderer.TIER_4_TEXTURE));
        registrar.register(ModEntityTypes.LANDER.get(), c -> new LanderRenderer(c, LanderModel.LAYER));

        registrar.register(ModEntityTypes.LUNARIAN.get(), LunarianRenderer::new);
        registrar.register(ModEntityTypes.CORRUPTED_LUNARIAN.get(), CorruptedLunarianRenderer::new);
        registrar.register(ModEntityTypes.STAR_CRAWLER.get(), StarCrawlerRenderer::new);
        registrar.register(ModEntityTypes.MARTIAN_RAPTOR.get(), MartianRaptorRenderer::new);
        registrar.register(ModEntityTypes.PYGRO.get(), PygroRenderer::new);
        registrar.register(ModEntityTypes.ZOMBIFIED_PYGRO.get(), ZombifiedPygroRenderer::new);
        registrar.register(ModEntityTypes.PYGRO_BRUTE.get(), PygroBruteRenderer::new);
        registrar.register(ModEntityTypes.MOGLER.get(), MoglerRenderer::new);
        registrar.register(ModEntityTypes.ZOMBIFIED_MOGLER.get(), ZombifiedMoglerRenderer::new);
        registrar.register(ModEntityTypes.SULFUR_CREEPER.get(), SulfurCreeperRenderer::new);
        registrar.register(ModEntityTypes.LUNARIAN_WANDERING_TRADER.get(), LunarianWanderingTraderRenderer::new);
        registrar.register(ModEntityTypes.GLACIAN_RAM.get(), GlacianRamRenderer::new);
        registrar.register(ModEntityTypes.ICE_SPIT.get(), ThrownItemRenderer::new);
    }

    public interface EntityRegistrar {
        <T extends Entity> void register(
            EntityType<? extends T> entityType, EntityRendererProvider<T> factory
        );
    }

    public static void registerArmor() {
        ClientPlatformUtils.registerArmor(SpaceSuitModel.SPACE_SUIT_TEXTURE, SpaceSuitModel.SPACE_SUIT_LAYER,
            ModItems.SPACE_HELMET.get(), ModItems.SPACE_SUIT.get(),
            ModItems.SPACE_PANTS.get(), ModItems.SPACE_BOOTS.get());
        ClientPlatformUtils.registerArmor(SpaceSuitModel.NETHERITE_SPACE_SUIT_TEXTURE, SpaceSuitModel.NETHERITE_SPACE_SUIT_LAYER,
            ModItems.NETHERITE_SPACE_HELMET.get(), ModItems.NETHERITE_SPACE_SUIT.get(),
            ModItems.NETHERITE_SPACE_PANTS.get(), ModItems.NETHERITE_SPACE_BOOTS.get());
        ClientPlatformUtils.registerArmor(SpaceSuitModel.JET_SUIT_TEXTURE, SpaceSuitModel.JET_SUIT_LAYER,
            ModItems.JET_SUIT_HELMET.get(), ModItems.JET_SUIT.get(),
            ModItems.JET_SUIT_PANTS.get(), ModItems.JET_SUIT_BOOTS.get());
    }

    public static void onRegisterEntityLayers(ClientPlatformUtils.LayerDefinitionRegistry consumer) {
        consumer.register(RoverModel.LAYER, RoverModel::createBodyLayer);
        RocketModel.register(consumer);
        consumer.register(LanderModel.LAYER, LanderModel::createBodyLayer);
        SpaceSuitModel.register(consumer);

        consumer.register(LunarianModel.LAYER_LOCATION, LunarianModel::createBodyLayer);
        consumer.register(CorruptedLunarianModel.LAYER_LOCATION, CorruptedLunarianModel::createBodyLayer);
        consumer.register(StarCrawlerModel.LAYER_LOCATION, StarCrawlerModel::createBodyLayer);
        consumer.register(MartianRaptorModel.LAYER_LOCATION, MartianRaptorModel::createBodyLayer);
        consumer.register(PygroModel.LAYER_LOCATION, PygroModel::createBodyLayer);
        consumer.register(PygroBruteModel.LAYER_LOCATION, PygroBruteModel::createBodyLayer);
        consumer.register(ZombifiedPygroModel.LAYER_LOCATION, ZombifiedPygroModel::createBodyLayer);
        consumer.register(MoglerModel.LAYER_LOCATION, MoglerModel::createBodyLayer);
        consumer.register(SulfurCreeperModel.LAYER_LOCATION, SulfurCreeperModel::createBodyLayer);
        consumer.register(GlacianRamModel.LAYER_LOCATION, GlacianRamModel::createBodyLayer);
    }

    public static void onRegisterParticles(BiConsumer<ParticleType<SimpleParticleType>, ClientPlatformUtils.SpriteParticleRegistration<SimpleParticleType>> consumer) {
        consumer.accept(ModParticleTypes.ACID_RAIN.get(), SplashParticle.Provider::new);
        consumer.accept(ModParticleTypes.LARGE_FLAME.get(), LargeFlameParticle.Provider::new);
        consumer.accept(ModParticleTypes.LARGE_SMOKE.get(), LargeFlameParticle.Provider::new);
        consumer.accept(ModParticleTypes.OXYGEN_BUBBLE.get(), OxygenBubbleParticle.Provider::new);
    }

    // Render layers (old registerRenderLayers) are gone: 26.x picks the chunk layer per quad from sprite transparency.
    // Item properties and item colors are now declared in assets/ad_astra/items/*.json.

    public static void onRegisterModels(Consumer<Identifier> consumer) {
        ModBlocks.GLOBES.stream().forEach(b -> consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/%s_cube".formatted(b.getId().getPath()))));
        consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/%s_flipped".formatted(ModBlocks.AIRLOCK.getId().getPath())));
        consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "block/%s_flipped".formatted(ModBlocks.REINFORCED_DOOR.getId().getPath())));
        consumer.accept(OxygenDistributorBlockEntityRenderer.TOP);
        consumer.accept(GravityNormalizerBlockEntityRenderer.TOP);
        consumer.accept(GravityNormalizerBlockEntityRenderer.TOE);
    }

    /**
     * Special item model types referenced by assets/ad_astra/items/*.json ({"type": "minecraft:special", "model": {"type": "ad_astra:<id>"}}).
     */
    public static void onRegisterSpecialRenderers(BiConsumer<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>> consumer) {
        ModBlocks.GLOBES.stream().forEach(globe -> consumer.accept(globe.getId(),
            AnimatedBlockItemRenderer.Unbaked.codec(globe, 20f, 360f, GlobeBlockEntityRenderer::submitPart)));
        consumer.accept(ModBlocks.OXYGEN_DISTRIBUTOR.getId(),
            AnimatedBlockItemRenderer.Unbaked.codec(ModBlocks.OXYGEN_DISTRIBUTOR, 5f, 360f, OxygenDistributorBlockEntityRenderer::submitPart));
        consumer.accept(ModBlocks.GRAVITY_NORMALIZER.getId(),
            AnimatedBlockItemRenderer.Unbaked.codec(ModBlocks.GRAVITY_NORMALIZER, 5f, 0f, GravityNormalizerBlockEntityRenderer::submitPart));
        consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "tier_1_rover"), RoverRenderer.ItemUnbaked.MAP_CODEC);
        for (int tier = 1; tier <= 4; tier++) {
            consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "tier_%d_rocket".formatted(tier)), RocketRenderer.ItemRenderer.Unbaked.CODECS.get(tier - 1));
        }
    }

    public static void onRegisterHud(BiConsumer<Identifier, HudElement> consumer) {
        consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "overlay"), OverlayScreen::render);
    }

    public static void renderOverlays(PoseStack stack, SubmitNodeCollector collector, Vec3 camera) {
        OXYGEN_OVERLAY_RENDERER.render(stack, collector, camera);
        GRAVITY_OVERLAY_RENDERER.render(stack, collector, camera);
    }

    public static void onAddReloadListener(BiConsumer<Identifier, PreparableReloadListener> consumer) {
        consumer.accept(Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, "planet_renderers"), new AdAstraPlanetRenderers());
    }

    public static void clientTick(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        if (KEY_OPEN_RADIO.consumeClick() && player.getVehicle() instanceof RadioHolder) {
            RadioHandler.open(null);
        }

        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItemTags.JET_SUITS)) {
            Options options = minecraft.options;

            if (KEY_TOGGLE_SUIT_FLIGHT.consumeClick()) {
                AdAstraConfigClient.jetSuitEnabled = !AdAstraConfigClient.jetSuitEnabled;
                Minecraft.getInstance().execute(() -> AdAstra.CONFIGURATOR.saveConfig(AdAstraConfigClient.class));
                player.sendOverlayMessage(AdAstraConfigClient.jetSuitEnabled ? ConstantComponents.SUIT_FLIGHT_ENABLED : ConstantComponents.SUIT_FLIGHT_DISABLED);
            }

            KeybindManager.set(player,
                options.keyJump.isDown(),
                options.keySprint.isDown(),
                AdAstraConfigClient.jetSuitEnabled);

            var chest = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            if (chest.getItem() instanceof earth.terrarium.adastra.common.items.armor.JetSuitItem suit) suit.tickFlight(player, chest);

            NetworkHandler.CHANNEL.sendToServer(new ServerboundSyncKeybindPacket(
                options.keyJump.isDown(),
                options.keySprint.isDown(),
                AdAstraConfigClient.jetSuitEnabled
            ));
        }
    }
}
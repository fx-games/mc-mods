package earth.terrarium.adastra.fabric;

import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.common.commands.AdAstraCommands;
import earth.terrarium.adastra.common.registry.ModEntityTypes;
import earth.terrarium.adastra.common.tags.ModItemTags;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import earth.terrarium.adastra.common.items.armor.JetSuitItem;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnPlacements;

public class AdAstraFabric {

    public static void init() {
        AdAstra.init();
        earth.terrarium.adastra.common.storage.EnergyApi.register();
        earth.terrarium.adastra.common.storage.FluidApi.register();
        onAddReloadListener();
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> AdAstra.onDatapackSync(player));
        ServerTickEvents.END_SERVER_TICK.register(AdAstra::onServerTick);
        ServerLifecycleEvents.SERVER_STARTED.register(AdAstra::onServerStarted);
        ModEntityTypes.registerAttributes((type, builder) -> FabricDefaultAttributeRegistry.register(type.get(), builder.get()));
        CommandRegistrationCallback.EVENT.register((dispatcher, ctx, environment) -> AdAstraCommands.register(dispatcher));

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            var stack = player.getItemInHand(hand);
            if (!level.isClientSide()
                && stack.is(ModItemTags.DESTROYED_IN_SPACE)
                && !OxygenApi.API.hasOxygen(level, hitResult.getBlockPos().immutable().relative(hitResult.getDirection()))
            ) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
        ModEntityTypes.registerSpawnPlacements(SpawnPlacements::register);

        // Replaces the removed FabricElytraItem interface (was JetSuitItemMixin).
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            var chest = entity.getItemBySlot(EquipmentSlot.CHEST);
            return ((Object) chest.getItem()) instanceof JetSuitItem jetSuit && jetSuit.canElytraFly(chest, entity);
        });
    }

    public static void onAddReloadListener() {
        AdAstra.onAddReloadListener((id, listener) -> ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id, listener));
    }
}

package earth.terrarium.adastra.client;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.adastra.client.models.armor.SpaceSuitModel;
import earth.terrarium.adastra.common.items.armor.JetSuitItem;
import earth.terrarium.adastra.common.storage.FluidResource;
import earth.terrarium.adastra.common.storage.ResourceStack;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ClientPlatformUtils {

    @FunctionalInterface
    public interface SpriteParticleRegistration<T extends ParticleOptions> {

        ParticleProvider<T> create(SpriteSet spriteSet);
    }

    @FunctionalInterface
    public interface LayerDefinitionRegistry {

        void register(ModelLayerLocation location, Supplier<LayerDefinition> definition);
    }

    private static final Map<Identifier, ExtraModelKey<BlockStateModel>> EXTRA_MODELS = new HashMap<>();
    private static final int[] NO_TINTS = new int[0];

    private static ExtraModelKey<BlockStateModel> key(Identifier id) {
        return EXTRA_MODELS.computeIfAbsent(id, i -> ExtraModelKey.create(i::toString));
    }

    /**
     * Registers a standalone block model (e.g. "ad_astra:block/oxygen_distributor_top") so it can be fetched with {@link #getModel(Identifier)}.
     */
    public static void registerExtraModel(ModelLoadingPlugin.Context ctx, Identifier id) {
        ctx.addModel(key(id), SimpleUnbakedExtraModel.blockStateModel(id));
    }

    @Nullable
    public static BlockStateModel getModel(Identifier id) {
        return Minecraft.getInstance().getModelManager().getModel(key(id));
    }

    public static BlockStateModel getModel(BlockState state) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
    }

    /**
     * Submits a block model with the cutout block sheet at the current pose.
     */
    public static void submitBlockModel(@Nullable BlockStateModel model, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay) {
        if (model == null) return;
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(42), parts);
        collector.submitBlockModel(poseStack, Sheets.cutoutBlockItemSheet(), parts, NO_TINTS, light, overlay, 0);
    }

    public static void submitBlockModel(@Nullable BlockStateModel model, PoseStack poseStack, SubmitNodeCollector collector, int light) {
        submitBlockModel(model, poseStack, collector, light, OverlayTexture.NO_OVERLAY);
    }

    public static void registerArmor(Identifier texture, ModelLayerLocation layer, Item... items) {
        ArmorRenderer.register(context -> {
            Map<EquipmentSlot, SpaceSuitModel> models = new EnumMap<>(EquipmentSlot.class);
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                models.put(slot, new SpaceSuitModel(context.bakeLayer(layer), slot));
            }
            return (poseStack, collector, stack, state, slot, light, contextModel) -> {
                SpaceSuitModel model = models.get(slot);
                if (model == null) return;

                if (stack.getItem() instanceof JetSuitItem suit) {
                    // ponytail: render state has no entity reference, so find the player at the rendered position.
                    var level = Minecraft.getInstance().level;
                    if (level != null) {
                        for (Player player : level.players()) {
                            if (player.distanceToSqr(state.x, state.y, state.z) < 1) {
                                suit.spawnParticles(level, player, contextModel, stack);
                                break;
                            }
                        }
                    }
                }

                int color = stack.has(net.minecraft.core.component.DataComponents.DYED_COLOR)
                    ? ARGB.opaque(DyedItemColor.getOrDefault(stack, DyedItemColor.LEATHER_COLOR))
                    : -1;
                ArmorRenderer.submitTransformCopyingModel(contextModel, state, model, state, false, collector, poseStack,
                    RenderTypes.entityTranslucent(texture), light, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor, null);
            };
        }, items);
    }

    public static TextureAtlasSprite getFluidSprite(ResourceStack<FluidResource> stack) {
        var state = stack.resource().getType().defaultFluidState();
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state).stillMaterial().sprite();
    }

    public static int getFluidColor(ResourceStack<FluidResource> stack) {
        return FluidVariantRendering.getColor(FluidVariant.of(stack.resource().getType()));
    }

    public static Component getDisplayName(ResourceStack<FluidResource> stack) {
        return FluidVariantAttributes.getName(FluidVariant.of(stack.resource().getType()));
    }
}

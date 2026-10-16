package earth.terrarium.adastra.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.client.renderers.textures.FlagImageTexture;
import earth.terrarium.adastra.client.renderers.textures.FlagUrlTexture;
import earth.terrarium.adastra.common.blockentities.flag.FlagBlockEntity;
import earth.terrarium.adastra.common.blockentities.flag.content.FlagContent;
import earth.terrarium.adastra.common.blockentities.flag.content.ImageContent;
import earth.terrarium.adastra.common.blockentities.flag.content.UrlContent;
import earth.terrarium.adastra.common.blocks.FlagBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity, FlagBlockEntityRenderer.State> {

    private static final Set<Identifier> REGISTERED_TEXTURES = new HashSet<>();

    private final SkullModelBase skullModel;
    private final PlayerSkinRenderCache skinCache;

    public FlagBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.skullModel = SkullBlockRenderer.createModel(context.entityModelSet(), SkullBlock.Types.PLAYER);
        this.skinCache = context.playerSkinRenderCache();
    }

    public static class State extends BlockEntityRenderState {
        public BlockState blockState = Blocks.AIR.defaultBlockState();
        public @Nullable RenderType flagImage;
        public RenderType skull = RenderTypes.entityCutoutZOffset(Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png"));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(FlagBlockEntity entity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        state.blockState = entity.getBlockState();
        FlagContent content = entity.getContent();
        state.flagImage = content == null ? null : getFlagImage(content);
        state.skull = entity.getOwner() != null
            ? skinCache.getOrDefault(entity.getOwner()).renderType()
            : SkullBlockRenderer.getSkullRenderType(SkullBlock.Types.PLAYER, null);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        var blockState = state.blockState;
        var direction = blockState.getValue(FlagBlock.FACING);
        int light = state.lightCoords;
        int overlay = OverlayTexture.NO_OVERLAY;

        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(direction.asRotation()));
        pose.translate(-0.5, 0, -0.5);

        if (blockState.getValue(FlagBlock.HALF).equals(DoubleBlockHalf.LOWER)) {
            ClientPlatformUtils.submitBlockModel(ClientPlatformUtils.getModel(blockState), pose, collector, light, overlay);
        } else if (state.flagImage == null) {
            pose.translate(-0.75, 0.19, 0.495);
            pose.scale(1, 1, 0.1f / 16f);
            if (skullModel != null) SkullBlockRenderer.submitSkull(0, pose, collector, light, skullModel, state.skull, 0, null);

            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(180));
            pose.translate(-0.5, 0, -0.5);
            pose.translate(0, 0, -1);

            if (skullModel != null) SkullBlockRenderer.submitSkull(0, pose, collector, light, skullModel, state.skull, 0, null);
        } else {
            Vec3i normal = direction.normal();

            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.XP.rotationDegrees(180));
            pose.translate(-0.5, 0, -0.5);
            pose.scale(1 + 5.8f / 16f, 1, 1);
            pose.translate(-11 / 16f, -15 / 16f, 0.495);
            collector.submitCustomGeometry(pose, state.flagImage, (p, consumer) -> renderQuad(p, normal, consumer, light, overlay));

            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(180));
            pose.translate(-0.5, 0, -0.5);
            pose.translate(0, 0, 0.99);
            collector.submitCustomGeometry(pose, state.flagImage, (p, consumer) -> renderQuad(p, normal, consumer, light, overlay));
        }
        pose.popPose();
    }

    private static void renderQuad(PoseStack.Pose pose, Vec3i normal, VertexConsumer consumer, int light, int overlay) {
        consumer.addVertex(pose, 0, 0, 0).setColor(255, 255, 255, 255).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(pose, normal.getX(), normal.getY(), normal.getZ());
        consumer.addVertex(pose, 0, 1, 0).setColor(255, 255, 255, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pose, normal.getX(), normal.getY(), normal.getZ());
        consumer.addVertex(pose, 1, 1, 0).setColor(255, 255, 255, 255).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(pose, normal.getX(), normal.getY(), normal.getZ());
        consumer.addVertex(pose, 1, 0, 0).setColor(255, 255, 255, 255).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(pose, normal.getX(), normal.getY(), normal.getZ());
    }

    private static RenderType getFlagImage(FlagContent content) {
        Identifier id = content.toTexture();
        if (REGISTERED_TEXTURES.add(id)) {
            if (content instanceof UrlContent url) {
                FlagUrlTexture.load(id, url.url());
            } else if (content instanceof ImageContent image) {
                Minecraft.getInstance().getTextureManager().register(id, new FlagImageTexture(image.data()));
            }
        }
        return RenderTypes.entitySolid(id);
    }
}

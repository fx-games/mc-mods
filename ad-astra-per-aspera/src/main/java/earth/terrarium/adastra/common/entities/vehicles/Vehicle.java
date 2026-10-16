package earth.terrarium.adastra.common.entities.vehicles;

import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider;
import earth.terrarium.adastra.api.systems.GravityApi;
import earth.terrarium.adastra.common.container.VehicleContainer;
import earth.terrarium.adastra.common.entities.multipart.MultipartEntity;
import earth.terrarium.adastra.common.entities.multipart.MultipartPartEntity;
import earth.terrarium.adastra.common.menus.content.EntityContent;
import earth.terrarium.adastra.common.network.NetworkHandler;
import earth.terrarium.adastra.common.network.packets.ServerboundVehicleControlPacket;
import earth.terrarium.adastra.mixins.common.LivingEntityAccessor;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class Vehicle extends Entity implements PlayerRideable, ContentMenuProvider<EntityContent>, MultipartEntity, HasCustomInventoryScreen {

    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);

    private float xxa;
    private float zza;

    protected final VehicleContainer inventory = new VehicleContainer(getInventorySize());

    protected final List<VehiclePart> parts = new ArrayList<>();
    private final List<MultipartPartEntity<?>> multipartParts = new ArrayList<>();

    public Vehicle(EntityType<?> type, Level level) {
        super(type, level);
    }

    protected void addPart(float width, float height, Vector3f offset, BiFunction<Player, InteractionHand, InteractionResult> handler) {
        VehiclePart part = new VehiclePart(this, width, height, offset, handler);
        this.parts.add(part);
        this.multipartParts.add(part);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        int i = 0;
        for (ItemStack stack : input.listOrEmpty("Inventory", ItemStack.OPTIONAL_CODEC)) {
            if (i >= inventory.getContainerSize()) break;
            inventory.setItem(i++, stack);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        var list = output.list("Inventory", ItemStack.OPTIONAL_CODEC);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            list.add(inventory.getItem(i));
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return AbstractBoat.canVehicleCollide(this, entity);
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity other) {
        return true;
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return interpolation;
    }

    @Override
    public void tick() {
        super.tick();
        this.tickLerp();
        if (!isNoGravity()) tickGravity();
        setPos(getX(), getY(), getZ());
        if (isLocalInstanceAuthoritative()) {
            if (level().isClientSide() && isVehicle() && getControllingPassenger() instanceof Player player) {
                NetworkHandler.CHANNEL.sendToServer(new ServerboundVehicleControlPacket(player.xxa, player.zza));
            }
            move(MoverType.SELF, getDeltaMovement());
            tickFriction();
        }

        for (VehiclePart part : this.parts) {
            part.tickPart();
        }
    }

    public void tickLerp() {
        if (isLocalInstanceAuthoritative()) {
            interpolation.cancel();
            syncPacketPositionCodec(getX(), getY(), getZ());
        }
        interpolation.interpolate();
    }

    public void tickGravity() {
        Vec3 velocity = getDeltaMovement();
        double gravity = 0.05 * GravityApi.API.getGravity(this);
        setDeltaMovement(velocity.x, velocity.y - gravity, velocity.z);
    }

    public void tickFriction() {
        float friction = level().getBlockState(getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction();
        float speed = this.onGround() ? friction * 0.91f : 0.91f;

        var deltaMovement = getDeltaMovement();
        setDeltaMovement(deltaMovement.x * speed, deltaMovement.y, deltaMovement.z * speed);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return getFirstPassenger() instanceof LivingEntity e ? e : null;
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableToBase(source)) return false;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return false;
        if (amount >= 0
            && source.getEntity() instanceof Player player
            && (player.getVehicle() == null || !player.getVehicle().equals(this))) {
            playSound(SoundEvents.NETHERITE_BLOCK_BREAK);
            if (!player.getAbilities().instabuild) {
                drop();
            }
            discard();
            return true;
        }
        return false;
    }

    public void drop() {
        Containers.dropItemStack(level(), getX(), getY(), getZ(), getDropStack());
        if (inventory.getContainerSize() > 0) {
            Containers.dropContents(level(), blockPosition(), inventory);
        }
    }

    public abstract ItemStack getDropStack();

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!level().isClientSide()) {
            if (player.isSecondaryUseActive()) {
                this.openCustomInventoryScreen(player);
                return InteractionResult.SUCCESS;
            }
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer);
        }
    }

    public void updateInput(float xxa, float zza) {
        this.xxa = xxa;
        this.zza = zza;
    }

    public abstract int getInventorySize();

    /**
     * Checks if the rider should be sitting in the vehicle.
     */
    public boolean shouldSit() {
        return true;
    }

    /**
     * Prevents the rider from rendering.
     */
    public boolean hideRider() {
        return false;
    }

    public boolean zoomOutCameraInThirdPerson() {
        return false;
    }

    /**
     * Checks if it's safe to dismount the vehicle. If not, the passenger has to hold shift for 2 seconds to dismount.
     */
    public boolean isSafeToDismount(Player player) {
        return true;
    }

    /**
     * Gets the left/right passenger input.
     */
    public float xxa() {
        var controllingPassenger = getControllingPassenger();
        if (controllingPassenger == null) return 0;
        return level().isClientSide() ? controllingPassenger.xxa : xxa;
    }

    /**
     * Gets the forward/backward passenger input.
     */
    public float zza() {
        var controllingPassenger = getControllingPassenger();
        if (controllingPassenger == null) return 0;
        return level().isClientSide() ? controllingPassenger.zza : zza;
    }

    public boolean passengerHasSpaceDown() {
        var controllingPassenger = getControllingPassenger();
        if (!(controllingPassenger instanceof LivingEntityAccessor entity)) return false;
        return entity.isJumping();
    }

    public VehicleContainer inventory() {
        return inventory;
    }

    @Override
    public EntityContent createContent(ServerPlayer serverPlayer) {
        return new EntityContent(getId());
    }

    @Override
    public List<MultipartPartEntity<?>> getMultiParts() {
        return multipartParts;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        for (int i = 0; i < this.parts.size(); ++i) {
            this.parts.get(i).setId(i + 1 + packet.getId());
        }
    }
}

package earth.terrarium.adastra.common.items.armor;

import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.common.utils.KeybindManager;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.adastra.common.storage.ItemContext;
import earth.terrarium.adastra.common.storage.ModifyOnlyContext;
import earth.terrarium.adastra.common.storage.EnergyApi;
import earth.terrarium.adastra.common.storage.EnergyProvider;
import earth.terrarium.adastra.common.storage.SimpleValueStorage;
import earth.terrarium.adastra.common.storage.FluidAmounts;
import earth.terrarium.adastra.common.storage.ValueStorage;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class JetSuitItem extends SpaceSuitItem implements EnergyProvider.Item {

    private final long energyCapacity;

    public JetSuitItem(ArmorMaterial material, ArmorType type, int tankSize, int energy, Item.Properties properties) {
        super(material, type, tankSize, properties);
        this.energyCapacity = energy;
    }

    @Override
    public ValueStorage getEnergy(ItemStack itemStack, ItemContext context) {
        return new SimpleValueStorage(context, ModDataManagers.VALUE_CONTENT.componentType(), energyCapacity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        List<Component> tooltipComponents = new java.util.ArrayList<>();
        tooltipComponents.add(TooltipUtils.getFluidComponent(
            FluidUtils.getTank(stack),
            FluidAmounts.toPlatformAmount(tankSize),
            ModFluids.OXYGEN.get()));
        var energy = new ModifyOnlyContext(stack).find(EnergyApi.ITEM);
        tooltipComponents.add(TooltipUtils.getEnergyComponent(energy.getStoredAmount(), energyCapacity));
//        tooltipComponents.add(TooltipUtils.getMaxEnergyInComponent(energy.maxInsert()));
        TooltipUtils.addDescriptionComponent(tooltipComponents, ConstantComponents.JET_SUIT_INFO);
        tooltipComponents.forEach(tooltip);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (entity instanceof Player player) tickFlight(player, stack);
    }

    /** Runs on both sides; the client calls it from AdAstraClient.clientTick since inventoryTick is server-only. */
    public void tickFlight(Player player, ItemStack stack) {
        if (player.getItemBySlot(EquipmentSlot.CHEST) != stack) return;

        if (player.getAbilities().flying) return;
        if (player.getCooldowns().isOnCooldown(stack)) return;
        if (!hasFullJetSuitSet(player)) return;

        if (!KeybindManager.suitFlightEnabled(player)) return;
        if (!KeybindManager.jumpDown(player)) return;
        if (!canFly(player, stack)) return;

        if (KeybindManager.sprintDown(player)) {
            fullFlight(player);
            consume(player, stack, 100, EquipmentSlot.CHEST);
        } else {
            upwardsFlight(player);
            consume(player, stack, 50, EquipmentSlot.CHEST);
        }
    }

    protected void upwardsFlight(Player player) {
        double acceleration = sigmoidAcceleration(player.tickCount, 5.0, 1.0, 2.0);
        acceleration /= 25.0f;
        player.addDeltaMovement(new Vec3(0, Math.max(0.0025, acceleration), 0));
        player.fallDistance = Math.max(player.fallDistance / 1.5f, 0.0f);
    }

    protected void fullFlight(Player player) {
        Vec3 movement = player.getLookAngle().normalize().scale(0.075);
        if (player.getDeltaMovement().length() > 2.0) return;
        player.addDeltaMovement(movement);
        player.fallDistance = Math.max(player.fallDistance / 1.5f, 0.0f);
        if (!player.isFallFlying()) {
            player.startFallFlying();
        }
    }

    private boolean canFly(Player player, ItemStack stack) {
        var energy = new ModifyOnlyContext(stack).find(EnergyApi.ITEM);
        return player.isCreative() || energy.getStoredAmount() > 0;
    }

    private void consume(Player player, ItemStack stack, int amount, EquipmentSlot slot) {
        if (player.isCreative() || player.isSpectator() || player.level().isClientSide()) return;
        var itemContext = new ModifyOnlyContext(stack);
        var container = itemContext.find(EnergyApi.ITEM);
        if (container == null) return;
        container.extract(amount, false);
        player.setItemSlot(slot, itemContext.stack());
    }

    protected boolean isFullFlightEnabled(Player player) {
        return KeybindManager.suitFlightEnabled(player) && KeybindManager.jumpDown(player) && KeybindManager.sprintDown(player);
    }

    public static double sigmoidAcceleration(double t, double peakTime, double peakAcceleration, double initialAcceleration) {
        return ((2 * peakAcceleration) / (1 + Math.exp(-t / peakTime)) - peakAcceleration) + initialAcceleration;
    }

    public void spawnParticles(Level level, LivingEntity entity, HumanoidModel<?> model, ItemStack stack) {
        if (!(entity instanceof Player player)) return;
        if (!canFly(player, stack)) return;
        if (!hasFullJetSuitSet(player)) return;
        if (!KeybindManager.suitFlightEnabled(player)) return;
        if (!KeybindManager.jumpDown(player) || (!KeybindManager.jumpDown(player) && !KeybindManager.sprintDown(player)))
            return;

        spawnParticles(level, entity, model.rightArm.xRot + 0.05, entity.isFallFlying() ? 0.0 : 0.8, -0.45);
        spawnParticles(level, entity, model.leftArm.xRot + 0.05, entity.isFallFlying() ? 0.0 : 0.8, 0.45);
        spawnParticles(level, entity, model.rightLeg.xRot + 0.05, entity.isFallFlying() ? 0.1 : 0.0, -0.1);
        spawnParticles(level, entity, model.leftLeg.xRot + 0.05, entity.isFallFlying() ? 0.1 : 0.0, 0.1);
    }

    // Spawns particles at the limbs of the player
    private void spawnParticles(Level level, LivingEntity entity, double pitch, double yOffset, double zOffset) {
        double yRot = entity.yBodyRot;
        double forwardOffsetX = Math.cos(yRot * Math.PI / 180) * zOffset;
        double forwardOffsetZ = Math.sin(yRot * Math.PI / 180) * zOffset;
        double sideOffsetX = Math.cos((yRot - 90) * Math.PI / 180) * pitch;
        double sideOffsetZ = Math.sin((yRot - 90) * Math.PI / 180) * pitch;

        level.addParticle(ParticleTypes.FLAME, true, false,
            entity.getX() + forwardOffsetX + sideOffsetX,
            entity.getY() + yOffset,
            entity.getZ() + sideOffsetZ + forwardOffsetZ,
            0, 0, 0);
    }

    @SuppressWarnings("unused") // NeoForge
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (entity.level().isClientSide()) return true;
        if (this.type != ArmorType.CHESTPLATE) return true;
        int nextFlightTick = flightTicks + 1;
        if (nextFlightTick % 10 != 0) return true;

        if (nextFlightTick % 20 == 0) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
        }

        entity.gameEvent(GameEvent.ELYTRA_GLIDE);

        return true;
    }

    @SuppressWarnings("unused") // NeoForge
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return entity instanceof Player player && canFly(player, stack) && isFullFlightEnabled(player);
    }
}
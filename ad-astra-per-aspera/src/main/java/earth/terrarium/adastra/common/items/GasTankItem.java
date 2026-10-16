package earth.terrarium.adastra.common.items;

import net.minecraft.world.InteractionResult;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.adastra.common.storage.ItemContext;
import earth.terrarium.adastra.common.storage.ModifyOnlyContext;
import earth.terrarium.adastra.common.storage.FluidApi;
import earth.terrarium.adastra.common.storage.SimpleFluidStorage;
import earth.terrarium.adastra.common.storage.FluidProvider;
import earth.terrarium.adastra.common.storage.FluidResource;
import earth.terrarium.adastra.common.storage.FluidAmounts;
import earth.terrarium.adastra.common.storage.CommonStorage;
import earth.terrarium.adastra.common.storage.TransferUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GasTankItem extends Item implements FluidProvider.Item {

    private final long tankSize;
    private final long distributionAmount;

    public GasTankItem(Properties properties, long tankSize, long distributionAmount) {
        super(properties);
        this.tankSize = tankSize;
        this.distributionAmount = distributionAmount;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (FluidUtils.hasFluid(stack)) {
            player.startUsingItem(usedHand);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide()) return;
        if (!(entity instanceof Player player)) return;
        Inventory inventory = player.getInventory();
        var container = new ModifyOnlyContext(stack).find(FluidApi.ITEM);
        if (container == null || container.getAmount(0) == 0) return;
        if (!distributeSequential(container, inventory)) return;
        if (entity.tickCount % 4 == 0) {
            level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK.value(), player.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public boolean distributeSequential(CommonStorage<FluidResource> from, Inventory inventory) {
        for (int i = inventory.getContainerSize() - 1; i >= 0; i--) {
            var stack = inventory.getItem(i);
            if (stack.isEmpty() || stack.is(this)) continue;
            var context = new ModifyOnlyContext(stack);
            if (!context.isPresent(FluidApi.ITEM)) continue;
            var to = context.find(FluidApi.ITEM);
            var toMove = from.getContents(0).withCount(FluidAmounts.toPlatformAmount(distributionAmount));
            long moved = TransferUtil.move(from, to, toMove.resource(), toMove.amount(), false);
            if (moved > 0) return true;
        }
        return false;
    }

    @Override
    public CommonStorage<FluidResource> getFluids(ItemStack itemStack, ItemContext context) {
        return new SimpleFluidStorage(context, ModDataManagers.FLUID_CONTENTS.componentType(), 1, FluidAmounts.toPlatformAmount(tankSize));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag isAdvanced) {
        List<Component> tooltipComponents = new ArrayList<>();
        tooltipComponents.add(TooltipUtils.getFluidComponent(FluidUtils.getTank(stack), FluidUtils.getTankCapacity(stack)));
        tooltipComponents.add(TooltipUtils.getMaxFluidOutComponent(FluidAmounts.toPlatformAmount(distributionAmount)));
        TooltipUtils.addDescriptionComponent(tooltipComponents, ConstantComponents.GAS_TANK_INFO);
        tooltipComponents.forEach(tooltip);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72_000;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return FluidUtils.hasFluid(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        var fluidContainer = new ModifyOnlyContext(stack).find(FluidApi.ITEM);
        if (fluidContainer == null) return 0;
        return (int) (((double) fluidContainer.getAmount(0) / fluidContainer.getLimit(0, FluidResource.BLANK)) * 13);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return ClientPlatformUtils.getFluidColor(FluidUtils.getTank(stack));
    }
}

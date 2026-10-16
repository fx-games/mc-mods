package earth.terrarium.adastra.common.items.vehicles;

import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import earth.terrarium.adastra.client.ClientPlatformUtils;
import earth.terrarium.adastra.common.items.rendered.RenderedItem;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.tags.ModFluidTags;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public abstract class VehicleItem extends RenderedItem implements FluidProvider.Item {

    private final Supplier<EntityType<?>> type;

    public VehicleItem(Supplier<EntityType<?>> type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public EntityType<?> type() {
        return type.get();
    }

    @Override
    public CommonStorage<FluidResource> getFluids(ItemStack itemStack, ItemContext context) {
        return new SimpleFluidStorage(context, ModDataManagers.FLUID_CONTENTS.componentType(), 1, FluidAmounts.toPlatformAmount(3000))
            .filter(0, f -> f.is(ModFluidTags.FUEL));
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag isAdvanced) {
        List<Component> tooltipComponents = new ArrayList<>();
        tooltipComponents.add(TooltipUtils.getFluidComponent(
            FluidUtils.getTank(stack),
            FluidUtils.getTankCapacity(stack),
            ModFluids.FUEL.get()));
        tooltipComponents.forEach(tooltip);
    }
}

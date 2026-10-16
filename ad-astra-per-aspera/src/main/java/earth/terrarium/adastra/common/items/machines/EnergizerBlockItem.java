package earth.terrarium.adastra.common.items.machines;

import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import earth.terrarium.adastra.common.blockentities.machines.EnergizerBlockEntity;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.adastra.common.storage.ItemContext;
import earth.terrarium.adastra.common.storage.ModifyOnlyContext;
import earth.terrarium.adastra.common.storage.EnergyApi;
import earth.terrarium.adastra.common.storage.EnergyProvider;
import earth.terrarium.adastra.common.storage.SimpleValueStorage;
import earth.terrarium.adastra.common.storage.ValueStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnergizerBlockItem extends BlockItem implements EnergyProvider.Item {

    public EnergizerBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack, BlockState state) {
        if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof EnergizerBlockEntity entity)) {
            return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        }

        var itemEnergyContainer = new ModifyOnlyContext(stack).find(EnergyApi.ITEM);
        if (itemEnergyContainer == null) return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        entity.getEnergyStorage().insert(itemEnergyContainer.getStoredAmount(), false);
        entity.onEnergyChange();

        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public ValueStorage getEnergy(ItemStack itemStack, ItemContext context) {
        return new SimpleValueStorage(context, ModDataManagers.VALUE_CONTENT.componentType(), 2_000_000);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        var energyStorage = new ModifyOnlyContext(stack).find(EnergyApi.ITEM);
        return energyStorage.getStoredAmount() > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        var energyStorage = new ModifyOnlyContext(stack).find(EnergyApi.ITEM);
        return (int) (((double) energyStorage.getStoredAmount() / energyStorage.getCapacity()) * 13);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x63dcc2;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        List<Component> tooltipComponents = new ArrayList<>();
        var energy = new ModifyOnlyContext(stack).find(EnergyApi.ITEM);
        tooltipComponents.add(TooltipUtils.getEnergyComponent(energy.getStoredAmount(), energy.getCapacity()));
//        tooltipComponents.add(TooltipUtils.getMaxEnergyInComponent(energy.maxInsert()));
//        tooltipComponents.add(TooltipUtils.getMaxEnergyOutComponent(energy.maxExtract()));
        TooltipUtils.addDescriptionComponent(tooltipComponents, ConstantComponents.ENERGIZER_INFO);
        tooltipComponents.forEach(tooltip);
    }
}

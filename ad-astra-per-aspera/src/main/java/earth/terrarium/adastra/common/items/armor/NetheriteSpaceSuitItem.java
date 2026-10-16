package earth.terrarium.adastra.common.items.armor;

import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.adastra.common.storage.FluidAmounts;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class NetheriteSpaceSuitItem extends SpaceSuitItem {

    public NetheriteSpaceSuitItem(ArmorMaterial material, ArmorType type, long tankSize, Properties properties) {
        super(material, type, tankSize, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        List<Component> tooltipComponents = new java.util.ArrayList<>();
        tooltipComponents.add(TooltipUtils.getFluidComponent(
            FluidUtils.getTank(stack),
            FluidAmounts.toPlatformAmount(tankSize),
            ModFluids.OXYGEN.get()));
        TooltipUtils.addDescriptionComponent(tooltipComponents, ConstantComponents.NETHERITE_SPACE_SUIT_INFO);
        tooltipComponents.forEach(tooltip);
    }
}

package earth.terrarium.adastra.common.items;

import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TooltipBlockItem extends BlockItem {

    private final Component description;

    public TooltipBlockItem(Block block, Component description, Properties properties) {
        super(block, properties);
        this.description = description;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag isAdvanced) {
        List<Component> tooltipComponents = new ArrayList<>();
        TooltipUtils.addDescriptionComponent(tooltipComponents, description);
        tooltipComponents.forEach(tooltip);
    }
}

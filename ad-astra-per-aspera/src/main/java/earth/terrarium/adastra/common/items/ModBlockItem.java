package earth.terrarium.adastra.common.items;

import earth.terrarium.adastra.common.blocks.base.TooltipBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** BlockItem that shows the tooltip of a {@link TooltipBlock}. */
public class ModBlockItem extends BlockItem {

    public ModBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        if (getBlock() instanceof TooltipBlock block) {
            List<Component> list = new ArrayList<>();
            block.appendHoverText(stack, context, list, flag);
            list.forEach(tooltip);
        }
    }
}

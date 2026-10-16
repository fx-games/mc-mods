package earth.terrarium.adastra.common.blocks.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Block#appendHoverText was removed in 26.x; blocks implementing this get their tooltip shown by {@link earth.terrarium.adastra.common.items.ModBlockItem}.
 */
public interface TooltipBlock {
    void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag);
}

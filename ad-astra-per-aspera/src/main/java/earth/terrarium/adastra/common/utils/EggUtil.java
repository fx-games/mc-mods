package earth.terrarium.adastra.common.utils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class EggUtil {

    // 26.x: spawn egg colors are no longer item properties (egg textures/tints come from the item model).
    public static Supplier<Item> createEgg(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Item.Properties properties) {
        return () -> new SpawnEggItem(properties.spawnEgg(type.get()));
    }
}

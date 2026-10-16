package earth.terrarium.adastra.common.utils;

import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;

public class PlatformUtils {

    public static Entity teleportToDimension(Entity entity, ServerLevel level, TeleportTransition transition) {
        return entity.teleport(transition);
    }

    public static Supplier<Item> createSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Item.Properties properties) {
        return EggUtil.createEgg(type, primaryColor, secondaryColor, properties);
    }

    // 26.x: fuel values are per level (Level#fuelValues), so a level is required now.
    public static int getBurnTime(Level level, ItemStack burnable) {
        return level.fuelValues().burnDuration(burnable);
    }
}

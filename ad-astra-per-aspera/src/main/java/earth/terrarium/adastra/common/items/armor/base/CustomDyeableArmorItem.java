package earth.terrarium.adastra.common.items.armor.base;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class CustomDyeableArmorItem extends Item {

    protected final ArmorMaterial material;
    protected final ArmorType type;

    public CustomDyeableArmorItem(ArmorMaterial material, ArmorType type, Properties properties) {
        super(properties
            .humanoidArmor(material, type)
            .component(DataComponents.DYED_COLOR, new DyedItemColor(0xFFFFFFFF)));
        this.material = material;
        this.type = type;
    }

    public ArmorMaterial getMaterial() {
        return material;
    }

    public ArmorType getType() {
        return type;
    }
}

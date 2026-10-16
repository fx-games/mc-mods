package earth.terrarium.adastra.common.items.armor.materials;

import com.google.common.collect.Maps;
import earth.terrarium.adastra.AdAstra;
import earth.terrarium.adastra.common.tags.ModItemTags;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class ModArmorMaterials {
    public static final ArmorMaterial SPACE_SUIT = new ArmorMaterial(37, defense(2, 5, 6, 2, 5), 14,
        SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, ModItemTags.STEEL_INGOTS, asset("space_suit"));
    public static final ArmorMaterial NETHERITE_SPACE_SUIT = new ArmorMaterial(37, defense(3, 7, 8, 3, 11), 15,
        SoundEvents.ARMOR_EQUIP_NETHERITE, 3, 0.1F, ItemTags.REPAIRS_NETHERITE_ARMOR, asset("netherite_space_suit"));
    public static final ArmorMaterial JET_SUIT = new ArmorMaterial(42, defense(4, 7, 9, 4, 11), 15,
        SoundEvents.ARMOR_EQUIP_NETHERITE, 5, 0.1F, ModItemTags.CALORITE_INGOTS, asset("jet_suit"));

    private static Map<ArmorType, Integer> defense(int boots, int legs, int chest, int helm, int body) {
        return Maps.newEnumMap(Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helm, ArmorType.BODY, body));
    }

    // Equipment asset json (assets/ad_astra/equipment/<name>.json) is expected by the renderer.
    private static ResourceKey<EquipmentAsset> asset(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(AdAstra.MOD_ID, name));
    }
}

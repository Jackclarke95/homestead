package jackclarke95.homestead.item;

import jackclarke95.homestead.Homestead;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item CHEESE_WHEEL = registerItem("cheese_wheel", new Item(new Item.Settings()));
    public static final Item RAW_HIDE = registerItem("raw_hide", new Item(new Item.Settings()));
    public static final Item SALT = registerItem("salt", new Item(new Item.Settings()));

    // Food
    public static final Item SUSPICIOUS_JERKY = registerItem("suspicious_jerky",
            new Item(new Item.Settings().food(ModFoodComponents.SUSPICIOUS_JERKY)));
    public static final Item BEEF_JERKY = registerItem("beef_jerky",
            new Item(new Item.Settings().food(ModFoodComponents.BEEF_JERKY)));
    public static final Item CHEESE_SLICE = registerItem("cheese_slice",
            new Item(new Item.Settings().food(ModFoodComponents.CHEESE_SLICE)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Homestead.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Homestead.LOGGER.info("Registering Mod Items for " + Homestead.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(SUSPICIOUS_JERKY);
            entries.add(BEEF_JERKY);
            entries.add(CHEESE_WHEEL);
            entries.add(CHEESE_SLICE);
            entries.add(RAW_HIDE);
            entries.add(SALT);
        });
    }
}

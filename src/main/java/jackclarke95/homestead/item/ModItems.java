package jackclarke95.homestead.item;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.item.custom.CrookItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
        public static final Item CHEESE_WHEEL = registerItem("cheese_wheel", new Item(new Item.Settings()));
        public static final Item RAW_HIDE = registerItem("raw_hide", new Item(new Item.Settings()));
        public static final Item MUG = registerItem("mug", new Item(new Item.Settings()));

        // Food
        public static final Item CHEESE_SLICE = registerItem("cheese_slice",
                        new Item(new Item.Settings().food(ModFoodComponents.CHEESE_SLICE)));
        public static final Item SUSPICIOUS_JERKY = registerItem("suspicious_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.SUSPICIOUS_JERKY)));
        public static final Item BEEF_JERKY = registerItem("beef_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.BEEF_JERKY)));
        public static final Item PORK_JERKY = registerItem("pork_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.PORK_JERKY)));
        public static final Item MUTTON_JERKY = registerItem("mutton_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.MUTTON_JERKY)));
        public static final Item CHICKEN_JERKY = registerItem("chicken_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.CHICKEN_JERKY)));
        public static final Item RABBIT_JERKY = registerItem("rabbit_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.RABBIT_JERKY)));
        public static final Item COD_JERKY = registerItem("cod_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.COD_JERKY)));
        public static final Item SALMON_JERKY = registerItem("salmon_jerky",
                        new Item(new Item.Settings().food(ModFoodComponents.SALMON_JERKY)));

        public static final Item PEAR = registerItem("pear",
                        new Item(new Item.Settings().food(ModFoodComponents.PEAR)));
        public static final Item PLUM = registerItem("plum",
                        new Item(new Item.Settings().food(ModFoodComponents.PLUM)));
        public static final Item LEMON = registerItem("lemon",
                        new Item(new Item.Settings().food(ModFoodComponents.LEMON)));
        public static final Item ORANGE = registerItem("orange",
                        new Item(new Item.Settings().food(ModFoodComponents.ORANGE)));
        public static final Item APRICOT = registerItem("apricot",
                        new Item(new Item.Settings().food(ModFoodComponents.APRICOT)));
        public static final Item PEACH = registerItem("peach",
                        new Item(new Item.Settings().food(ModFoodComponents.PEACH)));

        public static final Item MUG_OF_VODKA = registerItem("mug_of_vodka",
                        new Item(new Item.Settings().food(ModFoodComponents.MUG_OF_VODKA)));

        public static final Item FLOUR = registerItem("flour", new Item(new Item.Settings()));
        public static final Item SEED_MIX = registerItem("seed_mix", new Item(new Item.Settings()));
        public static final Item ANIMAL_FEED = registerItem("animal_feed", new Item(new Item.Settings()));

        public static final Item WOODEN_CROOK = registerItem("wooden_crook",
                        new CrookItem(ToolMaterials.WOOD, new Item.Settings()));
        public static final Item STONE_CROOK = registerItem("stone_crook",
                        new CrookItem(ToolMaterials.STONE, new Item.Settings()));
        public static final Item IRON_CROOK = registerItem("iron_crook",
                        new CrookItem(ToolMaterials.IRON, new Item.Settings()));
        public static final Item GOLDEN_CROOK = registerItem("golden_crook",
                        new CrookItem(ToolMaterials.GOLD, new Item.Settings()));
        public static final Item DIAMOND_CROOK = registerItem("diamond_crook",
                        new CrookItem(ToolMaterials.DIAMOND, new Item.Settings()));
        public static final Item NETHERITE_CROOK = registerItem("netherite_crook",
                        new CrookItem(ToolMaterials.NETHERITE, new Item.Settings().fireproof()));

        private static Item registerItem(String name, Item item) {
                return Registry.register(Registries.ITEM, Identifier.of(Homestead.MOD_ID, name), item);
        }

        public static void registerModItems() {
                Homestead.LOGGER.info("Registering Mod Items for " + Homestead.MOD_ID);

                ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
                        entries.add(MUG);
                        entries.add(WOODEN_CROOK);
                        entries.add(STONE_CROOK);
                        entries.add(IRON_CROOK);
                        entries.add(GOLDEN_CROOK);
                        entries.add(DIAMOND_CROOK);
                        entries.add(NETHERITE_CROOK);
                });

                ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
                        entries.add(SUSPICIOUS_JERKY);
                        entries.add(BEEF_JERKY);
                        entries.add(CHEESE_WHEEL);
                        entries.add(CHEESE_SLICE);
                        entries.add(MUG_OF_VODKA);
                        entries.add(PORK_JERKY);
                        entries.add(MUTTON_JERKY);
                        entries.add(CHICKEN_JERKY);
                        entries.add(RABBIT_JERKY);
                        entries.add(COD_JERKY);
                        entries.add(SALMON_JERKY);
                        entries.add(PEAR);
                        entries.add(PLUM);
                        entries.add(LEMON);
                        entries.add(ORANGE);
                        entries.add(APRICOT);
                        entries.add(PEACH);
                });

                ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
                        entries.add(RAW_HIDE);
                        entries.add(FLOUR);
                        entries.add(SEED_MIX);
                        entries.add(ANIMAL_FEED);
                });
        }
}

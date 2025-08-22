package jackclarke95.homestead.block;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.custom.CustomBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block CURING_VAT = registerBlock("curing_vat",
            new Block(AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE)));

    public static final Block CUSTOM_BLOCK = registerBlock("custom_block",
            new CustomBlock(AbstractBlock.Settings.create()
                    .strength(1.0f).luminance(state -> state.get(CustomBlock.CLICKED) ? 15 : 0)));

    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);

        return Registry.register(Registries.BLOCK, Identifier.of(Homestead.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Homestead.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        Homestead.LOGGER.info("Registering Mod Blocks for " + Homestead.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.CURING_VAT);
            entries.add(ModBlocks.CUSTOM_BLOCK);
        });
    }
}

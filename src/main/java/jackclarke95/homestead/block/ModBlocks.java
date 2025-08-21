package jackclarke95.homestead.block;

import net.minecraft.block.AbstractBlock.Settings;
import jackclarke95.homestead.Homestead;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block RACK = registerBlock("rack",
            new RackBlock(Settings.copy(Blocks.OAK_FENCE)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Homestead.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Homestead.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        Homestead.LOGGER.info("Registering blocks for " + Homestead.MOD_ID);
    }
}

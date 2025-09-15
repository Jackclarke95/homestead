package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ModLootTableProvider extends FabricBlockLootTableProvider {

        public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<WrapperLookup> registryLookup) {
                super(dataOutput, registryLookup);
        }

        @Override
        public void generate() {
                addDrop(ModBlocks.CURING_VAT);
                addDrop(ModBlocks.RACK);
                addDrop(ModBlocks.TROUGH);
                addDrop(ModBlocks.MILL);
                addDrop(ModBlocks.PRESS);
                addDrop(ModBlocks.SAWDUST);
                addDrop(ModBlocks.COBBLESTONE_BRICKS);
                addDrop(ModBlocks.COBBLESTONE_BRICK_SLAB);
                addDrop(ModBlocks.COBBLESTONE_BRICK_WALL);
                addDrop(ModBlocks.COBBLESTONE_BRICK_STAIRS);
                addDrop(ModBlocks.COBBLESTONE_BRICK_PATH);
                addDrop(ModBlocks.HAY_STAIRS);
                addDrop(ModBlocks.STONE_PATH);
                addDrop(ModBlocks.COBBLESTONE_PATH);
                addDrop(ModBlocks.ANDESITE_PATH);
                addDrop(ModBlocks.POLISHED_ANDESITE_PATH);
                addDrop(ModBlocks.DIORITE_PATH);
                addDrop(ModBlocks.POLISHED_DIORITE_PATH);
                addDrop(ModBlocks.GRANITE_PATH);
                addDrop(ModBlocks.POLISHED_GRANITE_PATH);
                addDrop(ModBlocks.SAND_PATH);
                addDrop(ModBlocks.GRAVEL_PATH);
                addDrop(ModBlocks.OAK_PLANK_PATH);
                addDrop(ModBlocks.SPRUCE_PLANK_PATH);
                addDrop(ModBlocks.BIRCH_PLANK_PATH);
                addDrop(ModBlocks.JUNGLE_PLANK_PATH);
                addDrop(ModBlocks.ACACIA_PLANK_PATH);
                addDrop(ModBlocks.DARK_OAK_PLANK_PATH);
                addDrop(ModBlocks.MANGROVE_PLANK_PATH);
                addDrop(ModBlocks.CHERRY_PLANK_PATH);
                addDrop(ModBlocks.BAMBOO_PLANK_PATH);
                addDrop(ModBlocks.CRIMSON_PLANK_PATH);

                addDrop(ModBlocks.WARPED_PLANK_PATH);

                addDrop(ModBlocks.MOSSY_COBBLESTONE_PATH);
                addDrop(ModBlocks.STONE_BRICK_PATH);
                addDrop(ModBlocks.MOSSY_STONE_BRICK_PATH);
                addDrop(ModBlocks.SMOOTH_STONE_PATH);
                addDrop(ModBlocks.CHISELED_STONE_BRICK_PATH);
                addDrop(ModBlocks.DEEPSLATE_PATH);
                addDrop(ModBlocks.COBBLED_DEEPSLATE_PATH);
                addDrop(ModBlocks.DEEPSLATE_BRICK_PATH);
                addDrop(ModBlocks.DEEPSLATE_TILE_PATH);
                addDrop(ModBlocks.POLISHED_DEEPSLATE_PATH);
                addDrop(ModBlocks.POLISHED_TUFF_PATH);
                addDrop(ModBlocks.TUFF_BRICK_PATH);
                addDrop(ModBlocks.BRICKS_PATH);
                addDrop(ModBlocks.PACKED_MUD_PATH);
                addDrop(ModBlocks.MUD_BRICK_PATH);
                addDrop(ModBlocks.SMOOTH_SANDSTONE_PATH);
                addDrop(ModBlocks.SMOOTH_RED_SANDSTONE_PATH);
                addDrop(ModBlocks.PRISMARINE_PATH);
                addDrop(ModBlocks.PRISMARINE_BRICK_PATH);
                addDrop(ModBlocks.DARK_PRISMARINE_PATH);
                addDrop(ModBlocks.NETHERRACK_PATH);
                addDrop(ModBlocks.NETHER_BRICK_PATH);
                addDrop(ModBlocks.CHISELED_NETHER_BRICK_PATH);
                addDrop(ModBlocks.RED_NETHER_BRICK_PATH);
                addDrop(ModBlocks.SMOOTH_BASALT_PATH);
                addDrop(ModBlocks.BLACKSTONE_PATH);
                addDrop(ModBlocks.GILDED_BLACKSTONE_PATH);
                addDrop(ModBlocks.CHISELED_POLISHED_BLACKSTONE_PATH);
                addDrop(ModBlocks.POLISHED_BLACKSTONE_PATH);
                addDrop(ModBlocks.POLISHED_BLACKSTONE_BRICK_PATH);
                addDrop(ModBlocks.END_STONE_PATH);
                addDrop(ModBlocks.END_STONE_BRICK_PATH);
                addDrop(ModBlocks.PURPUR_BLOCK_PATH);
                addDrop(ModBlocks.PURPUR_PILLAR_PATH);
                addDrop(ModBlocks.QUARTZ_BLOCK_PATH);
                addDrop(ModBlocks.CHISELED_QUARTZ_BLOCK_PATH);
                addDrop(ModBlocks.QUARTZ_BRICK_PATH);
                addDrop(ModBlocks.SMOOTH_QUARTZ_BLOCK_PATH);
                addDrop(ModBlocks.COARSE_DIRT_PATH);

                addDrop(ModBlocks.HEATED_RACK, block -> LootTable.builder()
                                .pool(LootPool.builder()
                                                .with(ItemEntry.builder(ModBlocks.HEATED_RACK)
                                                                .conditionally(this.createSilkTouchCondition())))
                                .pool(LootPool.builder()
                                                .with(ItemEntry.builder(ModBlocks.RACK)
                                                                .conditionally(this.createWithoutSilkTouchCondition()))
                                                .rolls(ConstantLootNumberProvider.create(1)))
                                .pool(LootPool.builder()
                                                .with(ItemEntry.builder(Items.COAL)
                                                                .apply(SetCountLootFunction.builder(
                                                                                ConstantLootNumberProvider.create(2)))
                                                                .conditionally(this.createWithoutSilkTouchCondition()))
                                                .rolls(ConstantLootNumberProvider.create(1))));
        }
}

package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.custom.VerticleSlabBlock;
import jackclarke95.homestead.block.custom.FruitBearingLeaves;
import jackclarke95.homestead.util.VerticalSlabType;
import jackclarke95.homestead.item.ModItems;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.enchantment.Enchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
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
        addDrop(ModBlocks.THATCHED_HAY_STAIRS);
        addDrop(ModBlocks.THATCHED_HAY_SLAB);
        addDrop(ModBlocks.THATCHED_HAY_BLOCK);
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

        addDrop(ModBlocks.APPLE_TREE_LOG);
        addDrop(ModBlocks.APPLE_TREE_WOOD);
        addDrop(ModBlocks.STRIPPED_APPLE_TREE_LOG);
        addDrop(ModBlocks.STRIPPED_APPLE_TREE_WOOD);
        addDrop(ModBlocks.APPLE_TREE_PLANKS);
        addDrop(ModBlocks.APPLE_TREE_SAPLING);
        addDrop(ModBlocks.APPLE_TREE_LEAVES, block -> fruitLeavesDrops(ModBlocks.APPLE_TREE_LEAVES,
                ModBlocks.APPLE_TREE_SAPLING, 0.0625f, Items.APPLE));
        addDrop(ModBlocks.PEAR_TREE_LOG);
        addDrop(ModBlocks.PEAR_TREE_WOOD);
        addDrop(ModBlocks.STRIPPED_PEAR_TREE_LOG);
        addDrop(ModBlocks.STRIPPED_PEAR_TREE_WOOD);
        addDrop(ModBlocks.PEAR_TREE_PLANKS);
        addDrop(ModBlocks.PEAR_TREE_SAPLING);
        addDrop(ModBlocks.PEAR_TREE_LEAVES, block -> fruitLeavesDrops(ModBlocks.PEAR_TREE_LEAVES,
                ModBlocks.PEAR_TREE_SAPLING, 0.0625f, ModItems.PEAR));
        addDrop(ModBlocks.PLUM_TREE_LOG);
        addDrop(ModBlocks.PLUM_TREE_WOOD);
        addDrop(ModBlocks.STRIPPED_PLUM_TREE_LOG);
        addDrop(ModBlocks.STRIPPED_PLUM_TREE_WOOD);
        addDrop(ModBlocks.PLUM_TREE_PLANKS);
        addDrop(ModBlocks.PLUM_TREE_SAPLING);
        addDrop(ModBlocks.PLUM_TREE_LEAVES, block -> fruitLeavesDrops(ModBlocks.PLUM_TREE_LEAVES,
                ModBlocks.PLUM_TREE_SAPLING, 0.0625f, ModItems.PLUM));
        addDrop(ModBlocks.LEMON_TREE_LOG);
        addDrop(ModBlocks.LEMON_TREE_WOOD);
        addDrop(ModBlocks.STRIPPED_LEMON_TREE_LOG);
        addDrop(ModBlocks.STRIPPED_LEMON_TREE_WOOD);
        addDrop(ModBlocks.LEMON_TREE_PLANKS);
        addDrop(ModBlocks.LEMON_TREE_SAPLING);
        addDrop(ModBlocks.LEMON_TREE_LEAVES, block -> fruitLeavesDrops(ModBlocks.LEMON_TREE_LEAVES,
                ModBlocks.LEMON_TREE_SAPLING, 0.0625f, ModItems.LEMON));
        addDrop(ModBlocks.ORANGE_TREE_LOG);
        addDrop(ModBlocks.ORANGE_TREE_WOOD);
        addDrop(ModBlocks.STRIPPED_ORANGE_TREE_LOG);
        addDrop(ModBlocks.STRIPPED_ORANGE_TREE_WOOD);
        addDrop(ModBlocks.ORANGE_TREE_PLANKS);
        addDrop(ModBlocks.ORANGE_TREE_SAPLING);
        addDrop(ModBlocks.ORANGE_TREE_LEAVES, block -> fruitLeavesDrops(ModBlocks.ORANGE_TREE_LEAVES,
                ModBlocks.ORANGE_TREE_SAPLING, 0.0625f, ModItems.ORANGE));
        addDrop(ModBlocks.PEACH_TREE_LOG);
        addDrop(ModBlocks.PEACH_TREE_WOOD);
        addDrop(ModBlocks.STRIPPED_PEACH_TREE_LOG);
        addDrop(ModBlocks.STRIPPED_PEACH_TREE_WOOD);
        addDrop(ModBlocks.PEACH_TREE_PLANKS);
        addDrop(ModBlocks.PEACH_TREE_SAPLING);
        addDrop(ModBlocks.PEACH_TREE_LEAVES, block -> fruitLeavesDrops(ModBlocks.PEACH_TREE_LEAVES,
                ModBlocks.PEACH_TREE_SAPLING, 0.0625f, ModItems.PEACH));

        addDrop(ModBlocks.WATTLE_AND_DAUB, block -> verticalSlabDrops(ModBlocks.WATTLE_AND_DAUB));

        addDrop(ModBlocks.BLACKBERRY_BUSH, berryBushDrops(ModBlocks.BLACKBERRY_BUSH, ModItems.BLACKBERRY));
        addDrop(ModBlocks.RASPBERRY_BUSH, berryBushDrops(ModBlocks.RASPBERRY_BUSH, ModItems.RASPBERRY));
        addDrop(ModBlocks.STRAWBERRY_BUSH, berryBushDrops(ModBlocks.STRAWBERRY_BUSH, ModItems.STRAWBERRY));

        addDrop(ModBlocks.RHUBARB_CROP, cropDrops(ModBlocks.RHUBARB_CROP, ModItems.RHUBARB, ModItems.RHUBARB_BULB));
    }

    private LootTable.Builder cropDrops(Block cropBlock, Item cropItem, Item seedItem) {
        return this.applyExplosionDecay(cropBlock,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .with(ItemEntry.builder(cropItem))
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(cropBlock)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(CropBlock.AGE, 7)))
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(2.0F, 4.0F))))
                        .pool(LootPool.builder()
                                .with(ItemEntry.builder(seedItem))
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(cropBlock)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(CropBlock.AGE, 7)))
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(1.0F, 2.0F))))
                        .pool(LootPool.builder()
                                .with(ItemEntry.builder(seedItem))
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(cropBlock)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(CropBlock.AGE, 7)))
                                .apply(ApplyBonusLootFunction.binomialWithBonusCount(
                                        this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                                                .getOrThrow(Enchantments.FORTUNE),
                                        0.5714286F, 3)))
                        .pool(LootPool.builder()
                                .with(ItemEntry.builder(seedItem))
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(cropBlock)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(CropBlock.AGE, 7))
                                        .invert())));
    }

    private LootTable.Builder berryBushDrops(Block bushBlock, Item berryItem) {
        return this.applyExplosionDecay(bushBlock,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(bushBlock)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(SweetBerryBushBlock.AGE, 3)))
                                .with(ItemEntry.builder(berryItem))
                                .apply(SetCountLootFunction.builder(
                                        UniformLootNumberProvider.create(2.0F, 3.0F)))
                                .apply(ApplyBonusLootFunction.uniformBonusCount(
                                        this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                                                .getOrThrow(Enchantments.FORTUNE))))
                        .pool(LootPool.builder()
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(bushBlock)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(SweetBerryBushBlock.AGE, 2)))
                                .with(ItemEntry.builder(berryItem))
                                .apply(SetCountLootFunction
                                        .builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
                                .apply(ApplyBonusLootFunction
                                        .uniformBonusCount(
                                                this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                                                        .getOrThrow(Enchantments.FORTUNE)))));
    }

    private LootTable.Builder fruitLeavesDrops(Block leavesBlock, Block saplingBlock, float saplingChance,
            Item fruitItem) {
        return leavesDrops(leavesBlock, saplingBlock, saplingChance)
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(fruitItem)
                                .apply(SetCountLootFunction
                                        .builder(ConstantLootNumberProvider
                                                .create(1.0F)))
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(leavesBlock)
                                        .properties(StatePredicate.Builder
                                                .create()
                                                .exactMatch(FruitBearingLeaves.FRUIT_STAGE,
                                                        2)))
                                .conditionally(this
                                        .createWithoutShearsOrSilkTouchCondition())
                                .conditionally(MatchToolLootCondition
                                        .builder(ItemPredicate.Builder.create()
                                                .items(ModItems.WOODEN_CROOK,
                                                        ModItems.STONE_CROOK,
                                                        ModItems.IRON_CROOK,
                                                        ModItems.GOLDEN_CROOK,
                                                        ModItems.DIAMOND_CROOK,
                                                        ModItems.NETHERITE_CROOK))
                                        .invert())))
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(fruitItem)
                                .apply(SetCountLootFunction
                                        .builder(UniformLootNumberProvider
                                                .create(1.0F, 3.0F)))
                                .apply(ApplyBonusLootFunction.uniformBonusCount(
                                        this.registryLookup.getWrapperOrThrow(
                                                net.minecraft.registry.RegistryKeys.ENCHANTMENT)
                                                .getOrThrow(Enchantments.FORTUNE)))
                                .conditionally(BlockStatePropertyLootCondition
                                        .builder(leavesBlock)
                                        .properties(StatePredicate.Builder
                                                .create()
                                                .exactMatch(FruitBearingLeaves.FRUIT_STAGE,
                                                        2)))
                                .conditionally(this
                                        .createWithoutShearsOrSilkTouchCondition())
                                .conditionally(MatchToolLootCondition
                                        .builder(ItemPredicate.Builder.create()
                                                .items(ModItems.WOODEN_CROOK,
                                                        ModItems.STONE_CROOK,
                                                        ModItems.IRON_CROOK,
                                                        ModItems.GOLDEN_CROOK,
                                                        ModItems.DIAMOND_CROOK,
                                                        ModItems.NETHERITE_CROOK)))));
    }

    private LootTable.Builder verticalSlabDrops(Block slabBlock) {
        return LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(slabBlock)
                                .apply(SetCountLootFunction
                                        .builder(ConstantLootNumberProvider
                                                .create(2))
                                        .conditionally(BlockStatePropertyLootCondition
                                                .builder(slabBlock)
                                                .properties(StatePredicate.Builder
                                                        .create()
                                                        .exactMatch(VerticleSlabBlock.TYPE,
                                                                VerticalSlabType.FULL))))
                                .apply(SetCountLootFunction
                                        .builder(ConstantLootNumberProvider
                                                .create(1))
                                        .conditionally(BlockStatePropertyLootCondition
                                                .builder(slabBlock)
                                                .properties(StatePredicate.Builder
                                                        .create()
                                                        .exactMatch(VerticleSlabBlock.TYPE,
                                                                VerticalSlabType.HALF)))))
                        .apply(ExplosionDecayLootFunction.builder())
                        .rolls(ConstantLootNumberProvider.create(1)));
    }
}

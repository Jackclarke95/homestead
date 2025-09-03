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
                addDrop(ModBlocks.COBBLESTONE_BRICKS);
                addDrop(ModBlocks.COBBLESTONE_BRICK_SLAB);
                addDrop(ModBlocks.COBBLESTONE_BRICK_WALL);
                addDrop(ModBlocks.COBBLESTONE_BRICK_STAIRS);
                addDrop(ModBlocks.COBBLESTONE_BRICK_PATH);
                addDrop(ModBlocks.STONE_PATH);
                addDrop(ModBlocks.COBBLESTONE_PATH);
                addDrop(ModBlocks.HAY_STAIRS);

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

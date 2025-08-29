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

    // LootCondition.Builder notSilkTouch = InvertedLootCondition.builder(
    // MatchToolLootCondition.builder(
    // ItemPredicate.Builder.create()
    // .enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH,
    // net.minecraft.predicate.NumberRange.IntRange.atLeast(1)))));

    @Override
    public void generate() {
        addDrop(ModBlocks.CURING_VAT);
        addDrop(ModBlocks.CUSTOM_BLOCK);
        addDrop(ModBlocks.RACK);
        addDrop(ModBlocks.TROUGH);
        addDrop(ModBlocks.MILL);

        // Drying rack loot table: Silk Touch drops itself, else drops rack and 2 coal
        addDrop(ModBlocks.DRYING_RACK, block -> LootTable.builder()
                // Pool 1: Drop drying rack if Silk Touch
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(ModBlocks.DRYING_RACK)
                                .conditionally(this.createSilkTouchCondition())))
                // Pool 2: Drop rack and 2 coal if NOT Silk Touch
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(ModBlocks.RACK))
                        .conditionally(this.createWithoutSilkTouchCondition())
                        .rolls(ConstantLootNumberProvider.create(1)))
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(Items.COAL)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))))
                        .conditionally(this.createWithoutSilkTouchCondition())
                        .rolls(ConstantLootNumberProvider.create(1))));
    }
}

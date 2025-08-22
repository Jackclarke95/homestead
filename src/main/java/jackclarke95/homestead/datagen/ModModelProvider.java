package jackclarke95.homestead.datagen;

import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CURING_VAT);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.BEEF_JERKY, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHEESE_SLICE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHEESE_WHEEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_HIDE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SUSPICIOUS_JERKY, Models.GENERATED);
    }

}

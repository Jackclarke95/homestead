package jackclarke95.homestead.datagen;

import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.custom.CustomBlock;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.TexturedModel;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CURING_VAT);

        Identifier blockOffIdentifier = TexturedModel.CUBE_ALL.upload(ModBlocks.CUSTOM_BLOCK,
                blockStateModelGenerator.modelCollector);
        Identifier blockOnIdentifier = blockStateModelGenerator.createSubModel(ModBlocks.CUSTOM_BLOCK, "_on",
                Models.CUBE_ALL, TextureMap::all);

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.CUSTOM_BLOCK)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(CustomBlock.CLICKED, blockOnIdentifier,
                        blockOffIdentifier)));

        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.RACK);
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

package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHEESE_WHEEL)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .input('#', ModItems.CHEESE_SLICE)
                .criterion(hasItem(ModItems.CHEESE_SLICE), conditionsFromItem(ModItems.CHEESE_SLICE))
                .offerTo(exporter, Identifier.of(Homestead.MOD_ID, "cheese_wheel_from_cheese_slice"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHEESE_SLICE, 8)
                .input(ModItems.CHEESE_WHEEL)
                .criterion(hasItem(ModItems.CHEESE_WHEEL), conditionsFromItem(ModItems.CHEESE_WHEEL))
                .offerTo(exporter, Identifier.of(Homestead.MOD_ID, "cheese_slice_from_cheese_wheel"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CURING_VAT)
                .pattern("###")
                .pattern("# #")
                .pattern("XXX")
                .input('#', ItemTags.WOODEN_SLABS)
                .input('X', ItemTags.PLANKS)
                .criterion(hasItem(ModBlocks.CURING_VAT), conditionsFromItem(ModBlocks.CURING_VAT))
                .offerTo(exporter);
    }
}

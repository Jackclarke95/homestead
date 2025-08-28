package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModRecipeProvider extends FabricRecipeProvider {

        public ModRecipeProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
                super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter recipeExporter) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHEESE_WHEEL)
                                .pattern("###")
                                .pattern("# #")
                                .pattern("###")
                                .input('#', ModItems.CHEESE_SLICE)
                                .criterion(hasItem(ModItems.CHEESE_SLICE), conditionsFromItem(ModItems.CHEESE_SLICE))
                                .offerTo(recipeExporter,
                                                Identifier.of(Homestead.MOD_ID, "cheese_wheel_from_cheese_slice"));

                ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.CHEESE_SLICE, 8)
                                .input(ModItems.CHEESE_WHEEL)
                                .criterion(hasItem(ModItems.CHEESE_WHEEL), conditionsFromItem(ModItems.CHEESE_WHEEL))
                                .offerTo(recipeExporter,
                                                Identifier.of(Homestead.MOD_ID, "cheese_slice_from_cheese_wheel"));

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CURING_VAT)
                                .pattern("###")
                                .pattern("###")
                                .pattern("X X")
                                .input('#', ItemTags.PLANKS)
                                .input('X', Items.STICK)
                                .criterion(hasItem(ModBlocks.CURING_VAT), conditionsFromItem(ModBlocks.CURING_VAT))
                                .offerTo(recipeExporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RACK)
                                .pattern("###")
                                .pattern("X X")
                                .pattern("X X")
                                .input('#', Items.STRING)
                                .input('X', Items.STICK)
                                .criterion(hasItem(ModBlocks.RACK), conditionsFromItem(ModBlocks.RACK))
                                .offerTo(recipeExporter);

                offer2x2CompactingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICKS,
                                Blocks.COBBLESTONE);

                offerSlabRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICK_SLAB,
                                ModBlocks.COBBLESTONE_BRICKS);
                offerWallRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICK_WALL,
                                ModBlocks.COBBLESTONE_BRICKS);

                createStairsRecipe(ModBlocks.COBBLESTONE_BRICK_STAIRS, Ingredient.ofItems(ModBlocks.COBBLESTONE_BRICKS))
                                .criterion("has_cobblestone_bricks", conditionsFromItem(ModBlocks.COBBLESTONE_BRICKS))
                                .offerTo(recipeExporter);

                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICKS,
                                Blocks.COBBLESTONE);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_SLAB,
                                Blocks.COBBLESTONE);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_STAIRS,
                                Blocks.COBBLESTONE);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_WALL,
                                Blocks.COBBLESTONE);

                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_SLAB,
                                ModBlocks.COBBLESTONE_BRICKS);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_STAIRS,
                                ModBlocks.COBBLESTONE_BRICKS);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_WALL,
                                ModBlocks.COBBLESTONE_BRICKS);

                createStairsRecipe(ModBlocks.HAY_STAIRS, Ingredient.ofItems(Blocks.HAY_BLOCK))
                                .criterion("has_hay_block", conditionsFromItem(Blocks.HAY_BLOCK))
                                .offerTo(recipeExporter);
        }
}

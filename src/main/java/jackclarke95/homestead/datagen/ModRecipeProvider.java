package jackclarke95.homestead.datagen;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.datagen.recipe.CuringRecipeJsonBuilder;
import jackclarke95.homestead.datagen.recipe.DryingRecipeJsonBuilder;
import jackclarke95.homestead.datagen.recipe.MillingRecipeJsonBuilder;
import jackclarke95.homestead.datagen.recipe.RinsingRecipeJsonBuilder;
import jackclarke95.homestead.datagen.recipe.PressingRecipeJsonBuilder;
import jackclarke95.homestead.item.ModItems;
import jackclarke95.homestead.recipe.PressingRecipe.SecondaryMode;
import jackclarke95.homestead.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
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
                // #region Food Recipes
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
                // #endregion Food Recipes

                // #region Tool Recipes
                ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.WOODEN_CROOK)
                                .pattern("PPS")
                                .pattern("P S")
                                .pattern("  S")
                                .input('S', Items.STICK)
                                .input('P', ItemTags.PLANKS)
                                .criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
                                .offerTo(recipeExporter);
                ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.STONE_CROOK)
                                .pattern("CCS")
                                .pattern("C S")
                                .pattern("  S")
                                .input('S', Items.STICK)
                                .input('C', Blocks.COBBLESTONE)
                                .criterion("has_cobblestone", conditionsFromItem(Blocks.COBBLESTONE))
                                .criterion("has_stick", conditionsFromItem(Items.STICK))
                                .offerTo(recipeExporter);
                ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.IRON_CROOK)
                                .pattern("IIS")
                                .pattern("I S")
                                .pattern("  S")
                                .input('S', Items.STICK)
                                .input('I', Items.IRON_INGOT)
                                .criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
                                .criterion("has_stick", conditionsFromItem(Items.STICK))
                                .offerTo(recipeExporter);
                ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.GOLDEN_CROOK)
                                .pattern("GGS")
                                .pattern("G S")
                                .pattern("  S")
                                .input('S', Items.STICK)
                                .input('G', Items.GOLD_INGOT)
                                .criterion("has_gold_ingot", conditionsFromItem(Items.GOLD_INGOT))
                                .criterion("has_stick", conditionsFromItem(Items.STICK))
                                .offerTo(recipeExporter);
                ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.DIAMOND_CROOK)
                                .pattern("DDS")
                                .pattern("D S")
                                .pattern("  S")
                                .input('S', Items.STICK)
                                .input('D', Items.DIAMOND)
                                .criterion("has_diamond", conditionsFromItem(Items.DIAMOND))
                                .criterion("has_stick", conditionsFromItem(Items.STICK));
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItems(ModItems.DIAMOND_CROOK),
                                Ingredient.ofItems(Items.NETHERITE_INGOT),
                                RecipeCategory.TOOLS, ModItems.NETHERITE_CROOK)
                                .criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
                                .criterion("has_diamond_crook", conditionsFromItem(ModItems.DIAMOND_CROOK))
                                .offerTo(recipeExporter, getItemPath(ModItems.NETHERITE_CROOK) + "_smithing");
                // #endregion Tool Recipes

                // #region Misc Recipes
                ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ANIMAL_FEED, 1)
                                .input(ModItems.FLOUR)
                                .input(ModTags.ItemTags.ROOT_VEGETABLES)
                                .input(ModItems.SEED_MIX)
                                .criterion(hasItem(ModItems.FLOUR), conditionsFromItem(ModItems.FLOUR))
                                .criterion(hasItem(ModItems.SEED_MIX), conditionsFromItem(ModItems.SEED_MIX))
                                .criterion("has_root_vegetable", conditionsFromTag(ModTags.ItemTags.ROOT_VEGETABLES))
                                .offerTo(recipeExporter);
                // #endregion Misc Recipes

                // #region Workstation Recipes
                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.CURING_VAT)
                                .pattern("###")
                                .pattern("###")
                                .pattern("X X")
                                .input('#', ItemTags.PLANKS)
                                .input('X', Items.STICK)
                                .criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
                                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                                .offerTo(recipeExporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.MILL)
                                .pattern("#-#")
                                .pattern("XXX")
                                .pattern("XXX")
                                .input('#', ItemTags.WOODEN_SLABS)
                                .input('-', Items.IRON_INGOT)
                                .input('X', Items.STONE)
                                .criterion("has_wooden_slabs", conditionsFromTag(ItemTags.WOODEN_SLABS))
                                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                                .criterion(hasItem(Blocks.STONE), conditionsFromItem(Items.STONE))
                                .offerTo(recipeExporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.RACK)
                                .pattern("###")
                                .pattern("X X")
                                .pattern("X X")
                                .input('#', Items.STRING)
                                .input('X', Items.STICK)
                                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STRING))
                                .criterion(hasItem(Items.STRING), conditionsFromItem(Items.STICK))
                                .offerTo(recipeExporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.HEATED_RACK)
                                .pattern("#")
                                .pattern("X")
                                .input('#', ModBlocks.RACK)
                                .input('X', Blocks.CAMPFIRE)
                                .criterion(hasItem(ModBlocks.RACK), conditionsFromItem(ModBlocks.RACK))
                                .criterion(hasItem(Blocks.CAMPFIRE), conditionsFromItem(Blocks.CAMPFIRE))
                                .offerTo(recipeExporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.PRESS)
                                .pattern("III")
                                .pattern("PBP")
                                .pattern("SSS")
                                .input('I', Items.STRING)
                                .input('P', ItemTags.PLANKS)
                                .input('B', Blocks.BARREL)
                                .input('S', Blocks.STONE)
                                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                                .criterion("has_planks", conditionsFromTag(ItemTags.PLANKS))
                                .criterion(hasItem(Blocks.BARREL), conditionsFromItem(Blocks.BARREL))
                                .criterion(hasItem(Blocks.STONE), conditionsFromItem(Blocks.STONE))
                                .offerTo(recipeExporter);

                // #endregion Workstation Recipes

                // #region Building Block Recipes
                createStairsRecipe(ModBlocks.HAY_STAIRS, Ingredient.ofItems(Blocks.HAY_BLOCK))
                                .criterion("has_hay_block", conditionsFromItem(Blocks.HAY_BLOCK))
                                .offerTo(recipeExporter);

                ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WATTLE_AND_DAUB)
                                .pattern("X-X")
                                .pattern("-#-")
                                .pattern("X-X")
                                .input('X', Items.CLAY_BALL)
                                .input('-', Items.STICK)
                                .input('#', Items.WHEAT)
                                .criterion("has_wooden_slabs", conditionsFromTag(ItemTags.WOODEN_SLABS))
                                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                                .criterion(hasItem(Blocks.STONE), conditionsFromItem(Items.STONE))
                                .offerTo(recipeExporter, "wattle_and_daub_from_clay_balls");

                ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.WATTLE_AND_DAUB, 1)
                                .input(Blocks.MUD)
                                .input(Items.WHEAT)
                                .input(Items.STICK)
                                .criterion(hasItem(Blocks.MUD), conditionsFromItem(Blocks.MUD))
                                .criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT))
                                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                                .offerTo(recipeExporter, "wattle_and_daub_from_mud");

                // #region Cobblestone Brick Recipes
                offer2x2CompactingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICKS,
                                Blocks.COBBLESTONE);

                offerSlabRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICK_SLAB,
                                ModBlocks.COBBLESTONE_BRICKS);
                offerWallRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.COBBLESTONE_BRICK_WALL,
                                ModBlocks.COBBLESTONE_BRICKS);

                createStairsRecipe(ModBlocks.COBBLESTONE_BRICK_STAIRS, Ingredient.ofItems(ModBlocks.COBBLESTONE_BRICKS))
                                .criterion("has_cobblestone_bricks", conditionsFromItem(ModBlocks.COBBLESTONE_BRICKS))
                                .offerTo(recipeExporter);

                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICKS, Blocks.COBBLESTONE);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_SLAB, Blocks.COBBLESTONE, 2);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_STAIRS, Blocks.COBBLESTONE);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_WALL, Blocks.COBBLESTONE);

                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_SLAB, ModBlocks.COBBLESTONE_BRICKS, 2);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_STAIRS, ModBlocks.COBBLESTONE_BRICKS);
                offerStonecuttingRecipe(recipeExporter, RecipeCategory.BUILDING_BLOCKS,
                                ModBlocks.COBBLESTONE_BRICK_WALL, ModBlocks.COBBLESTONE_BRICKS);
                // #endregion Cobblestone Brick Recipes
                // #endregion Building Block Recipes

                // #region Rinsing Recipes
                // Mossy rinsing recipes
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE),
                                Blocks.COBBLESTONE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE_SLAB),
                                Blocks.COBBLESTONE_SLAB.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE_STAIRS),
                                Blocks.COBBLESTONE_STAIRS.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE_WALL),
                                Blocks.COBBLESTONE_WALL.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS),
                                Blocks.STONE_BRICKS.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICK_SLAB),
                                Blocks.STONE_BRICK_SLAB.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICK_STAIRS),
                                Blocks.STONE_BRICK_STAIRS.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICK_WALL),
                                Blocks.STONE_BRICK_WALL.asItem().getDefaultStack(), 60).offerTo(recipeExporter);

                // Wool rinsing recipes
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_GRAY_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GRAY_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLACK_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BROWN_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.RED_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.ORANGE_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.YELLOW_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIME_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GREEN_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.CYAN_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_BLUE_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLUE_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PURPLE_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MAGENTA_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PINK_WOOL),
                                Items.WHITE_WOOL.getDefaultStack(), 60).offerTo(recipeExporter);

                // Banner rinsing recipes
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_GRAY_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GRAY_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLACK_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BROWN_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.RED_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.ORANGE_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.YELLOW_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIME_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GREEN_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.CYAN_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_BLUE_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLUE_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PURPLE_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MAGENTA_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PINK_BANNER),
                                Items.WHITE_BANNER.getDefaultStack(), 60).offerTo(recipeExporter);

                // Carpet rinsing recipes
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_GRAY_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GRAY_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLACK_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BROWN_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.RED_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.ORANGE_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.YELLOW_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIME_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GREEN_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.CYAN_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_BLUE_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLUE_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PURPLE_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MAGENTA_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PINK_CARPET),
                                Items.WHITE_CARPET.getDefaultStack(), 60).offerTo(recipeExporter);

                // Concrete Power rinsing recipes
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.WHITE_CONCRETE_POWDER),
                                Blocks.WHITE_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_GRAY_CONCRETE_POWDER),
                                Blocks.LIGHT_GRAY_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GRAY_CONCRETE_POWDER),
                                Blocks.GRAY_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLACK_CONCRETE_POWDER),
                                Blocks.BLACK_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BROWN_CONCRETE_POWDER),
                                Blocks.BROWN_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.RED_CONCRETE_POWDER),
                                Blocks.RED_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.ORANGE_CONCRETE_POWDER),
                                Blocks.ORANGE_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.YELLOW_CONCRETE_POWDER),
                                Blocks.YELLOW_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIME_CONCRETE_POWDER),
                                Blocks.LIME_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GREEN_CONCRETE_POWDER),
                                Blocks.GREEN_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.CYAN_CONCRETE_POWDER),
                                Blocks.CYAN_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_BLUE_CONCRETE_POWDER),
                                Blocks.LIGHT_BLUE_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLUE_CONCRETE_POWDER),
                                Blocks.BLUE_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PURPLE_CONCRETE_POWDER),
                                Blocks.PURPLE_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MAGENTA_CONCRETE_POWDER),
                                Blocks.MAGENTA_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PINK_CONCRETE_POWDER),
                                Blocks.PINK_CONCRETE.asItem().getDefaultStack(), 60).offerTo(recipeExporter);

                // Shulker rinsing recipes
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.WHITE_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_GRAY_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GRAY_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLACK_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BROWN_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.RED_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.ORANGE_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.YELLOW_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIME_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.GREEN_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.CYAN_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.LIGHT_BLUE_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.BLUE_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PURPLE_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.MAGENTA_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                RinsingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.PINK_SHULKER_BOX),
                                Blocks.SHULKER_BOX.asItem().getDefaultStack(), 60).offerTo(recipeExporter);
                // #endregion Rinsing Recipes

                // #region Drying Recipes
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.BEEF),
                                ModItems.BEEF_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.CHICKEN),
                                ModItems.CHICKEN_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.COD),
                                ModItems.COD_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.MUTTON),
                                ModItems.MUTTON_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.PORKCHOP),
                                ModItems.PORK_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.RABBIT),
                                ModItems.RABBIT_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.SALMON),
                                ModItems.SALMON_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.ROTTEN_FLESH),
                                ModItems.SUSPICIOUS_JERKY.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(ModItems.RAW_HIDE),
                                Items.LEATHER.getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.WET_SPONGE),
                                Blocks.SPONGE.asItem().getDefaultStack(), 1200).offerTo(recipeExporter);
                DryingRecipeJsonBuilder.create(Ingredient.ofItems(Items.KELP),
                                Items.DRIED_KELP.getDefaultStack(), 1200).offerTo(recipeExporter);
                // #endregion Rinsing Recipes

                // #region Milling Recipes
                MillingRecipeJsonBuilder.create(Ingredient.ofItems(Items.WHEAT), ModItems.FLOUR.getDefaultStack(), 20)
                                .offerTo(recipeExporter);
                MillingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.HAY_BLOCK),
                                                ModItems.FLOUR.getDefaultStack().copyWithCount(9), 180)
                                .offerTo(recipeExporter);
                MillingRecipeJsonBuilder.create(Ingredient.fromTag(ModTags.ItemTags.CROP_SEEDS),
                                ModItems.SEED_MIX.getDefaultStack(), 20)
                                .offerTo(recipeExporter);
                // #endregion Milling Recipes

                // #region Curing Recipes
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.MILK_BUCKET),
                                ModItems.CHEESE_WHEEL.getDefaultStack(), 1200)
                                .ingredientCount(3)
                                .catalyst(Ingredient.ofItems(Items.BONE_MEAL))
                                .offerTo(recipeExporter);
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.PORKCHOP),
                                ModItems.RAW_HIDE.getDefaultStack(), 60)
                                .catalyst(Ingredient.ofItems(Items.BONE_MEAL))
                                .offerTo(recipeExporter);
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.MUTTON),
                                ModItems.RAW_HIDE.getDefaultStack(), 60)
                                .catalyst(Ingredient.ofItems(Items.BONE_MEAL))
                                .offerTo(recipeExporter);
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.CHICKEN),
                                ModItems.RAW_HIDE.getDefaultStack(), 60)
                                .catalyst(Ingredient.ofItems(Items.BONE_MEAL))
                                .offerTo(recipeExporter);
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.BEEF),
                                ModItems.RAW_HIDE.getDefaultStack(), 60)
                                .catalyst(Ingredient.ofItems(Items.BONE_MEAL))
                                .offerTo(recipeExporter);
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.RABBIT),
                                ModItems.RAW_HIDE.getDefaultStack(), 60)
                                .catalyst(Ingredient.ofItems(Items.BONE_MEAL))
                                .offerTo(recipeExporter);
                CuringRecipeJsonBuilder.create(Ingredient.ofItems(Items.POTATO),
                                ModItems.MUG_OF_VODKA.getDefaultStack().copyWithCount(8), 960)
                                .ingredientCount(16)
                                .catalyst(Ingredient.ofItems(Items.WATER_BUCKET))
                                .container(Ingredient.ofItems(ModItems.MUG))
                                .byproduct(Items.BUCKET.getDefaultStack())
                                .offerTo(recipeExporter);
                // #endregion Curing Recipes

                // #region Pressing Recipes
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Items.APPLE), Items.WHEAT.getDefaultStack(), 120)
                                .secondaryResult(Items.WHEAT_SEEDS.getDefaultStack())
                                .secondaryChance(0.1)
                                .secondaryMode(SecondaryMode.ADDITIONAL)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.STONE_BRICKS),
                                                ModBlocks.STONE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .secondaryResult(Blocks.CRACKED_STONE_BRICKS.asItem().getDefaultStack())
                                .secondaryChance(0.15)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Items.POISONOUS_POTATO),
                                                ModItems.MUG_OF_VODKA.getDefaultStack(), 120)
                                .ingredientCount(2)
                                .container(Ingredient.ofItems(ModItems.MUG))
                                .secondaryResult(Items.BEETROOT.getDefaultStack())
                                .secondaryChance(0.25)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SAND), ModBlocks.SAND_PATH.asItem().getDefaultStack(),
                                                120)
                                .secondaryChance(0.5)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.DEEPSLATE_BRICKS),
                                                ModBlocks.DEEPSLATE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .secondaryResult(Blocks.CRACKED_DEEPSLATE_BRICKS.asItem().getDefaultStack())
                                .secondaryChance(0.15)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);

                // Deepslate Tile Path
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.DEEPSLATE_TILES),
                                                ModBlocks.DEEPSLATE_TILE_PATH.asItem().getDefaultStack(), 120)
                                .secondaryResult(Blocks.CRACKED_DEEPSLATE_TILES.asItem().getDefaultStack())
                                .secondaryChance(0.15)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);

                // Nether Brick Path
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.NETHER_BRICKS),
                                                ModBlocks.NETHER_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .secondaryResult(Blocks.CRACKED_NETHER_BRICKS.asItem().getDefaultStack())
                                .secondaryChance(0.15)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);

                // Polished Blackstone Brick Path
                PressingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE_BRICKS),
                                ModBlocks.POLISHED_BLACKSTONE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .secondaryResult(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem().getDefaultStack())
                                .secondaryChance(0.15)
                                .secondaryMode(SecondaryMode.INSTEAD)
                                .offerTo(recipeExporter);

                // All other path blocks (no cracked variant)
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.STONE),
                                                ModBlocks.STONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.COARSE_DIRT),
                                                ModBlocks.COARSE_DIRT_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.COBBLESTONE),
                                                ModBlocks.COBBLESTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(ModBlocks.COBBLESTONE_BRICKS),
                                                ModBlocks.COBBLESTONE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.ANDESITE),
                                                ModBlocks.ANDESITE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE),
                                                ModBlocks.POLISHED_ANDESITE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.DIORITE),
                                                ModBlocks.DIORITE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.POLISHED_DIORITE),
                                                ModBlocks.POLISHED_DIORITE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.GRANITE),
                                                ModBlocks.GRANITE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.POLISHED_GRANITE),
                                                ModBlocks.POLISHED_GRANITE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.GRAVEL),
                                                ModBlocks.GRAVEL_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.OAK_PLANKS),
                                                ModBlocks.OAK_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SPRUCE_PLANKS),
                                                ModBlocks.SPRUCE_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.BIRCH_PLANKS),
                                                ModBlocks.BIRCH_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.JUNGLE_PLANKS),
                                                ModBlocks.JUNGLE_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.ACACIA_PLANKS),
                                                ModBlocks.ACACIA_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.DARK_OAK_PLANKS),
                                                ModBlocks.DARK_OAK_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.MANGROVE_PLANKS),
                                                ModBlocks.MANGROVE_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.CHERRY_PLANKS),
                                                ModBlocks.CHERRY_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.BAMBOO_PLANKS),
                                                ModBlocks.BAMBOO_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.CRIMSON_PLANKS),
                                                ModBlocks.CRIMSON_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.WARPED_PLANKS),
                                                ModBlocks.WARPED_PLANK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.MOSSY_COBBLESTONE),
                                                ModBlocks.MOSSY_COBBLESTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.MOSSY_STONE_BRICKS),
                                                ModBlocks.MOSSY_STONE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SMOOTH_STONE),
                                                ModBlocks.SMOOTH_STONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.CHISELED_STONE_BRICKS),
                                                ModBlocks.CHISELED_STONE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.DEEPSLATE),
                                                ModBlocks.DEEPSLATE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.COBBLED_DEEPSLATE),
                                                ModBlocks.COBBLED_DEEPSLATE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.POLISHED_DEEPSLATE),
                                                ModBlocks.POLISHED_DEEPSLATE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.POLISHED_TUFF),
                                                ModBlocks.POLISHED_TUFF_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.TUFF_BRICKS),
                                                ModBlocks.TUFF_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.BRICKS),
                                                ModBlocks.BRICKS_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.PACKED_MUD),
                                                ModBlocks.PACKED_MUD_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.MUD_BRICKS),
                                                ModBlocks.MUD_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SMOOTH_SANDSTONE),
                                                ModBlocks.SMOOTH_SANDSTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SMOOTH_RED_SANDSTONE),
                                                ModBlocks.SMOOTH_RED_SANDSTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.PRISMARINE),
                                                ModBlocks.PRISMARINE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.PRISMARINE_BRICKS),
                                                ModBlocks.PRISMARINE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.DARK_PRISMARINE),
                                                ModBlocks.DARK_PRISMARINE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.NETHERRACK),
                                                ModBlocks.NETHERRACK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.CHISELED_NETHER_BRICKS),
                                                ModBlocks.CHISELED_NETHER_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.RED_NETHER_BRICKS),
                                                ModBlocks.RED_NETHER_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SMOOTH_BASALT),
                                                ModBlocks.SMOOTH_BASALT_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.BLACKSTONE),
                                                ModBlocks.BLACKSTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.GILDED_BLACKSTONE),
                                                ModBlocks.GILDED_BLACKSTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder.create(Ingredient.ofItems(Blocks.CHISELED_POLISHED_BLACKSTONE),
                                ModBlocks.CHISELED_POLISHED_BLACKSTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.POLISHED_BLACKSTONE),
                                                ModBlocks.POLISHED_BLACKSTONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.END_STONE),
                                                ModBlocks.END_STONE_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.END_STONE_BRICKS),
                                                ModBlocks.END_STONE_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.PURPUR_BLOCK),
                                                ModBlocks.PURPUR_BLOCK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.PURPUR_PILLAR),
                                                ModBlocks.PURPUR_PILLAR_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.QUARTZ_BLOCK),
                                                ModBlocks.QUARTZ_BLOCK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.CHISELED_QUARTZ_BLOCK),
                                                ModBlocks.CHISELED_QUARTZ_BLOCK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.QUARTZ_BRICKS),
                                                ModBlocks.QUARTZ_BRICK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                PressingRecipeJsonBuilder
                                .create(Ingredient.ofItems(Blocks.SMOOTH_QUARTZ),
                                                ModBlocks.SMOOTH_QUARTZ_BLOCK_PATH.asItem().getDefaultStack(), 120)
                                .offerTo(recipeExporter);
                // #endregion Pressing Recipes
        }
}

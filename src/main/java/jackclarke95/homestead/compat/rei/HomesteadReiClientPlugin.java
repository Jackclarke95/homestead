package jackclarke95.homestead.compat.rei;

import java.util.List;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.recipe.DryingRecipe;
import jackclarke95.homestead.recipe.RinsingRecipe;
import jackclarke95.homestead.recipe.MillingRecipe;
import jackclarke95.homestead.recipe.CuringRecipe;
import jackclarke95.homestead.recipe.PressingRecipe;
import jackclarke95.homestead.recipe.ModRecipes;

public class HomesteadReiClientPlugin implements REIClientPlugin {
    public static final Identifier DRYING_ID = Identifier.of(Homestead.MOD_ID, "drying");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SimpleDryingCategory());
        registry.addWorkstations(SimpleDryingCategory.ID, EntryStacks.of(ModBlocks.RACK));
        registry.addWorkstations(SimpleDryingCategory.ID, EntryStacks.of(ModBlocks.HEATED_RACK));

        registry.add(new SimpleRinsingCategory());
        registry.addWorkstations(SimpleRinsingCategory.ID, EntryStacks.of(ModBlocks.RACK));
        registry.addWorkstations(SimpleRinsingCategory.ID, EntryStacks.of(ModBlocks.HEATED_RACK));

        registry.add(new SimpleMillingCategory());
        registry.addWorkstations(SimpleMillingCategory.ID, EntryStacks.of(ModBlocks.MILL));

        registry.add(new CuringCategory());
        registry.addWorkstations(CuringCategory.ID, EntryStacks.of(ModBlocks.CURING_VAT));

        registry.add(new PressingCategory());
        registry.addWorkstations(PressingCategory.ID, EntryStacks.of(ModBlocks.PRESS));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        List<RecipeEntry<DryingRecipe>> recipes = registry.getRecipeManager().listAllOfType(ModRecipes.HEATED_TYPE);
        for (RecipeEntry<DryingRecipe> entry : recipes) {
            registry.add(new SimpleDryingDisplay(entry.value()));
        }

        for (RecipeEntry<RinsingRecipe> entry : registry.getRecipeManager().listAllOfType(ModRecipes.RINSING_TYPE)) {
            registry.add(new SimpleRinsingDisplay(entry.value()));
        }

        for (RecipeEntry<MillingRecipe> entry : registry.getRecipeManager().listAllOfType(ModRecipes.MILLING_TYPE)) {
            registry.add(new SimpleMillingDisplay(entry.value()));
        }

        for (RecipeEntry<CuringRecipe> entry : registry.getRecipeManager().listAllOfType(ModRecipes.CURING_TYPE)) {
            registry.add(new CuringDisplay(entry.value()));
        }

        for (RecipeEntry<PressingRecipe> entry : registry.getRecipeManager().listAllOfType(ModRecipes.PRESSING_TYPE)) {
            registry.add(new PressingDisplay(entry.value()));
        }
    }
}

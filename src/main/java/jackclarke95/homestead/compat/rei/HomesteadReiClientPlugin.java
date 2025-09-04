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
import jackclarke95.homestead.recipe.ModRecipes;

public class HomesteadReiClientPlugin implements REIClientPlugin {
    public static final Identifier DRYING_ID = Identifier.of(Homestead.MOD_ID, "drying");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SimpleDryingCategory());
        registry.addWorkstations(SimpleDryingCategory.ID, EntryStacks.of(ModBlocks.RACK));
        registry.addWorkstations(SimpleDryingCategory.ID, EntryStacks.of(ModBlocks.HEATED_RACK));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        List<RecipeEntry<DryingRecipe>> recipes = registry.getRecipeManager().listAllOfType(ModRecipes.HEATED_TYPE);
        for (RecipeEntry<DryingRecipe> entry : recipes) {
            registry.add(new SimpleDryingDisplay(entry.value()));
        }
    }
}

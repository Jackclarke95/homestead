package jackclarke95.homestead.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.recipe.DryingRecipe;
import jackclarke95.homestead.recipe.ModRecipes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public class HomesteadEmiPlugin implements EmiPlugin {
    public static final Identifier DRYING_ID = Identifier.of(Homestead.MOD_ID, "drying");
    public static final EmiRecipeCategory DRYING_CATEGORY = new EmiRecipeCategory(DRYING_ID,
            EmiStack.of(ModBlocks.RACK));

    @Override
    public void register(EmiRegistry registry) {
        // Category and workstations
        registry.addCategory(DRYING_CATEGORY);
        registry.addWorkstation(DRYING_CATEGORY, EmiStack.of(ModBlocks.RACK));
        registry.addWorkstation(DRYING_CATEGORY, EmiStack.of(ModBlocks.HEATED_RACK));

        // Recipes
        for (RecipeEntry<DryingRecipe> entry : registry.getRecipeManager().listAllOfType(ModRecipes.HEATED_TYPE)) {
            registry.addRecipe(new DryingEmiRecipe(entry.id(), entry.value()));
        }
    }
}

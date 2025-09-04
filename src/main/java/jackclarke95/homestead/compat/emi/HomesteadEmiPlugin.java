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
    public static final Identifier RINSING_ID = Identifier.of(Homestead.MOD_ID, "rinsing");
    public static final EmiRecipeCategory RINSING_CATEGORY = new EmiRecipeCategory(RINSING_ID,
            EmiStack.of(ModBlocks.TROUGH));
    public static final Identifier MILLING_ID = Identifier.of(Homestead.MOD_ID, "milling");
    public static final EmiRecipeCategory MILLING_CATEGORY = new EmiRecipeCategory(MILLING_ID,
            EmiStack.of(ModBlocks.MILL));
    public static final Identifier CURING_ID = Identifier.of(Homestead.MOD_ID, "curing");
    public static final EmiRecipeCategory CURING_CATEGORY = new EmiRecipeCategory(CURING_ID,
            EmiStack.of(ModBlocks.CURING_VAT));
    public static final Identifier PRESSING_ID = Identifier.of(Homestead.MOD_ID, "pressing");
    public static final EmiRecipeCategory PRESSING_CATEGORY = new EmiRecipeCategory(PRESSING_ID,
            EmiStack.of(ModBlocks.PRESS));

    @Override
    public void register(EmiRegistry registry) {
        // Category and workstations
        registry.addCategory(DRYING_CATEGORY);
        registry.addWorkstation(DRYING_CATEGORY, EmiStack.of(ModBlocks.RACK));
        registry.addWorkstation(DRYING_CATEGORY, EmiStack.of(ModBlocks.HEATED_RACK));
        registry.addCategory(RINSING_CATEGORY);
        registry.addWorkstation(RINSING_CATEGORY, EmiStack.of(ModBlocks.TROUGH));
        registry.addCategory(MILLING_CATEGORY);
        registry.addWorkstation(MILLING_CATEGORY, EmiStack.of(ModBlocks.MILL));
        registry.addCategory(CURING_CATEGORY);
        registry.addWorkstation(CURING_CATEGORY, EmiStack.of(ModBlocks.CURING_VAT));
        registry.addCategory(PRESSING_CATEGORY);
        registry.addWorkstation(PRESSING_CATEGORY, EmiStack.of(ModBlocks.PRESS));

        // Recipes
        for (RecipeEntry<DryingRecipe> entry : registry.getRecipeManager().listAllOfType(ModRecipes.HEATED_TYPE)) {
            registry.addRecipe(new DryingEmiRecipe(entry.id(), entry.value()));
        }
        for (RecipeEntry<jackclarke95.homestead.recipe.RinsingRecipe> entry : registry.getRecipeManager()
                .listAllOfType(ModRecipes.RINSING_TYPE)) {
            registry.addRecipe(new RinsingEmiRecipe(entry.id(), entry.value()));
        }
        for (RecipeEntry<jackclarke95.homestead.recipe.MillingRecipe> entry : registry.getRecipeManager()
                .listAllOfType(ModRecipes.MILLING_TYPE)) {
            registry.addRecipe(new MillingEmiRecipe(entry.id(), entry.value()));
        }
        for (RecipeEntry<jackclarke95.homestead.recipe.CuringRecipe> entry : registry.getRecipeManager()
                .listAllOfType(ModRecipes.CURING_TYPE)) {
            registry.addRecipe(new CuringEmiRecipe(entry.id(), entry.value()));
        }
        for (RecipeEntry<jackclarke95.homestead.recipe.PressingRecipe> entry : registry.getRecipeManager()
                .listAllOfType(ModRecipes.PRESSING_TYPE)) {
            registry.addRecipe(new PressingEmiRecipe(entry.id(), entry.value()));
        }
    }
}

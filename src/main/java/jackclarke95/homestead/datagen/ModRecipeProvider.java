package jackclarke95.homestead.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import jackclarke95.homestead.Homestead;
import jackclarke95.homestead.recipe.DryingRecipe;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Generate drying recipes
        createDryingRecipe(exporter, Items.ROTTEN_FLESH, Items.LEATHER, 60, "rotten_flesh_to_leather");
        createDryingRecipe(exporter, Items.KELP, Items.DRIED_KELP, 20, "kelp_to_dried_kelp");
        createDryingRecipe(exporter, Items.COD, Items.COOKED_COD, 80, "cod_to_dried_cod");
        createDryingRecipe(exporter, Items.PORKCHOP, Items.COOKED_PORKCHOP, 50, "porkchop_to_cooked_porkchop");
        createDryingRecipe(exporter, Items.BEEF, Items.COOKED_BEEF, 40, "beef_to_cooked_beef");
        createDryingRecipe(exporter, Items.CHICKEN, Items.COOKED_CHICKEN, 35, "chicken_to_cooked_chicken");
        createDryingRecipe(exporter, Items.MUTTON, Items.COOKED_MUTTON, 45, "mutton_to_cooked_mutton");
        createDryingRecipe(exporter, Items.RABBIT, Items.COOKED_RABBIT, 30, "rabbit_to_cooked_rabbit");
        createDryingRecipe(exporter, Items.SALMON, Items.COOKED_SALMON, 75, "salmon_to_cooked_salmon");

        // Add some additional drying recipes for variety
        createDryingRecipe(exporter, Items.WET_SPONGE, Items.SPONGE, 100, "wet_sponge_to_sponge");
        createDryingRecipe(exporter, Items.POTATO, Items.BAKED_POTATO, 90, "potato_to_baked_potato");
    }

    private void createDryingRecipe(RecipeExporter exporter, net.minecraft.item.ItemConvertible input,
            net.minecraft.item.ItemConvertible output, int dryingTime, String name) {
        DryingRecipe recipe = new DryingRecipe(
                Ingredient.ofItems(input),
                new ItemStack(output, 1),
                dryingTime);

        exporter.accept(Identifier.of(Homestead.MOD_ID, name), recipe, null);
    }

    @Override
    public String getName() {
        return "Drying Rack Recipes";
    }
}

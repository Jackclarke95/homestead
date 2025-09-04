package jackclarke95.homestead.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.Identifier;
import jackclarke95.homestead.Homestead;

// Minimal JEI plugin to ensure JEI loads without errors; categories/recipes can be added later.
public final class HomesteadJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.of(Homestead.MOD_ID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // No categories yet
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // No recipes yet
    }
}

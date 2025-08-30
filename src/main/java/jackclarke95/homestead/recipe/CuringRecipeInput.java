package jackclarke95.homestead.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record CuringRecipeInput(ItemStack ingredient, ItemStack catalyst, ItemStack container)
        implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> ingredient;
            case 1 -> catalyst;
            case 2 -> container;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int getSize() {
        return 3;
    }
}

package jackclarke95.homestead.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record MillingRecipeInput(ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? input : ItemStack.EMPTY;
    }
    @Override
    public int getSize() {
        return 1;
    }
}

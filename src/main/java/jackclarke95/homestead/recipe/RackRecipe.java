package jackclarke95.homestead.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record RackRecipe(Ingredient inputItem, ItemStack output) implements Recipe<RackRecipeInput> {
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();

        list.add(inputItem);

        return list;
    }

    @Override
    public boolean matches(RackRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }

        return inputItem.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(RackRecipeInput input, WrapperLookup lookup) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(WrapperLookup registriesLookup) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RACK_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RACK_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<RackRecipe> {
        public static final MapCodec<RackRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(RackRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(RackRecipe::output)).apply(inst, RackRecipe::new));

        public static final PacketCodec<RegistryByteBuf, RackRecipe> STREAM_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, RackRecipe::inputItem,
                ItemStack.PACKET_CODEC, RackRecipe::output,
                RackRecipe::new);

        @Override
        public MapCodec<RackRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, RackRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

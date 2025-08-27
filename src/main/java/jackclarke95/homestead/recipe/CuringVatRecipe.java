package jackclarke95.homestead.recipe;

import com.mojang.serialization.Codec;
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

public record CuringVatRecipe(Ingredient inputItem, ItemStack output, int time)
        implements Recipe<CuringVatRecipeInput> {

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputItem);

        return list;
    }

    @Override
    public boolean matches(CuringVatRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }

        if (inputItem.test(input.getStackInSlot(0))) {
            return true;
        }

        return false;
    }

    @Override
    public ItemStack craft(CuringVatRecipeInput input, WrapperLookup lookup) {
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
        return ModRecipes.CURING_VAT_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CURING_VAT_TYPE;
    }

    public static class Serializer implements RecipeSerializer<CuringVatRecipe> {
        public static final MapCodec<CuringVatRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(CuringVatRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(CuringVatRecipe::output),
                Codec.INT.fieldOf("time").forGetter(CuringVatRecipe::time))
                .apply(inst, CuringVatRecipe::new));

        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());

        public static final PacketCodec<RegistryByteBuf, CuringVatRecipe> STREAM_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, CuringVatRecipe::inputItem,
                ItemStack.PACKET_CODEC, CuringVatRecipe::output,
                INT_CODEC, CuringVatRecipe::time,
                CuringVatRecipe::new);

        @Override
        public MapCodec<CuringVatRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CuringVatRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

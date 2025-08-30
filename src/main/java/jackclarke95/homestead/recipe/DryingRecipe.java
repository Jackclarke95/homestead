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

public record DryingRecipe(Ingredient inputItem, ItemStack output, int time) implements Recipe<RackRecipeInput> {
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
        return ModRecipes.HEATED_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HEATED_TYPE;
    }

    public static class Serializer implements RecipeSerializer<DryingRecipe> {
        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(DryingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(DryingRecipe::output),
                Codec.INT.fieldOf("time").forGetter(DryingRecipe::time))
                .apply(inst, DryingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());

        public static final PacketCodec<RegistryByteBuf, DryingRecipe> STREAM_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, DryingRecipe::inputItem,
                ItemStack.PACKET_CODEC, DryingRecipe::output,
                INT_CODEC, DryingRecipe::time,
                DryingRecipe::new);

        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, DryingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

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

public record MillingRecipe(
        Ingredient input,
        ItemStack output,
        int time) implements Recipe<MillingRecipeInput> {
    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(input);
        return list;
    }

    @Override
    public boolean matches(MillingRecipeInput input, World world) {
        if (world.isClient())
            return false;
        return this.input.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(MillingRecipeInput input, WrapperLookup lookup) {
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
        return ModRecipes.MILLING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MILLING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<MillingRecipe> {
        public static final MapCodec<MillingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(MillingRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(MillingRecipe::output),
                Codec.INT.fieldOf("time").forGetter(MillingRecipe::time))
                .apply(inst, MillingRecipe::new));
        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());
        public static final PacketCodec<RegistryByteBuf, MillingRecipe> STREAM_CODEC = new PacketCodec<>() {
            @Override
            public void encode(RegistryByteBuf buf, MillingRecipe recipe) {
                Ingredient.PACKET_CODEC.encode(buf, recipe.input());
                ItemStack.PACKET_CODEC.encode(buf, recipe.output());
                INT_CODEC.encode(buf, recipe.time());
            }

            @Override
            public MillingRecipe decode(RegistryByteBuf buf) {
                Ingredient input = Ingredient.PACKET_CODEC.decode(buf);
                ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
                int time = INT_CODEC.decode(buf);
                return new MillingRecipe(input, output, time);
            }
        };

        @Override
        public MapCodec<MillingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MillingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

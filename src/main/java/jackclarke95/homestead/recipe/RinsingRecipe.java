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

public record RinsingRecipe(Ingredient inputItem, ItemStack output, int time) implements Recipe<RackRecipeInput> {
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
        ItemStack inputStack = input.getStackInSlot(0);
        ItemStack outputStack = output.copy();

        outputStack.copyComponentsToNewStack(inputStack.getItem(), inputStack.getCount());

        return outputStack;

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
        return ModRecipes.RINSING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RINSING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<RinsingRecipe> {
        public static final MapCodec<RinsingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(RinsingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(RinsingRecipe::output),
                Codec.INT.fieldOf("time").forGetter(RinsingRecipe::time))
                .apply(inst, RinsingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());

        public static final PacketCodec<RegistryByteBuf, RinsingRecipe> STREAM_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, RinsingRecipe::inputItem,
                ItemStack.PACKET_CODEC, RinsingRecipe::output,
                INT_CODEC, RinsingRecipe::time,
                RinsingRecipe::new);

        @Override
        public MapCodec<RinsingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, RinsingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

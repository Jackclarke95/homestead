package jackclarke95.homestead.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class DryingRecipe implements Recipe<SingleStackRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack result;
    private final int dryingTime;

    public DryingRecipe(Ingredient ingredient, ItemStack result, int dryingTime) {
        this.ingredient = ingredient;
        this.result = result;
        this.dryingTime = dryingTime;
    }

    @Override
    public boolean matches(SingleStackRecipeInput input, World world) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.DRYING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.DRYING;
    }

    public int getDryingTime() {
        return dryingTime;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public static class Serializer implements RecipeSerializer<DryingRecipe> {
        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(DryingRecipe::getIngredient),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                com.mojang.serialization.Codec.INT.fieldOf("drying_time").forGetter(DryingRecipe::getDryingTime))
                .apply(instance, DryingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, DryingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                Serializer::write, Serializer::read);

        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, DryingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static DryingRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            int dryingTime = buf.readVarInt();
            return new DryingRecipe(ingredient, result, dryingTime);
        }

        private static void write(RegistryByteBuf buf, DryingRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
            buf.writeVarInt(recipe.dryingTime);
        }
    }
}

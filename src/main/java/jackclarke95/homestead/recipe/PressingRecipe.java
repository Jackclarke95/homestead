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

/**
 * A recipe for pressing, similar to curing but without catalyst or byproduct.
 */
public record PressingRecipe(
        Ingredient inputItem,
        int ingredientCount,
        Ingredient container,
        ItemStack output,
        int time) implements Recipe<ContainerRecipeInput> {

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputItem);
        if (!container.isEmpty())
            list.add(container);
        return list;
    }

    @Override
    public boolean matches(ContainerRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }
        // Ingredient is always required
        if (!inputItem.test(input.getStackInSlot(0))) {
            return false;
        }
        // Container is not required for matching, only for output transfer
        return true;
    }

    @Override
    public ItemStack craft(ContainerRecipeInput input, WrapperLookup lookup) {
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

    public boolean hasContainer() {
        return !container.isEmpty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PRESSING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.PRESSING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<PressingRecipe> {
        public static final MapCodec<PressingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(PressingRecipe::inputItem),
                Codec.INT.optionalFieldOf("ingredientCount", 1).forGetter(PressingRecipe::ingredientCount),
                Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("container", Ingredient.EMPTY)
                        .forGetter(r -> r.container),
                ItemStack.CODEC.fieldOf("result").forGetter(PressingRecipe::output),
                Codec.INT.fieldOf("time").forGetter(PressingRecipe::time))
                .apply(inst, (ingredient, ingredientCount, container, result, time) -> new PressingRecipe(ingredient,
                        ingredientCount, container, result, time)));

        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());

        public static final PacketCodec<RegistryByteBuf, PressingRecipe> STREAM_CODEC = new PacketCodec<>() {
            @Override
            public void encode(RegistryByteBuf buf, PressingRecipe recipe) {
                Ingredient.PACKET_CODEC.encode(buf, recipe.inputItem());
                INT_CODEC.encode(buf, recipe.ingredientCount());
                Ingredient.PACKET_CODEC.encode(buf, recipe.container());
                ItemStack.PACKET_CODEC.encode(buf, recipe.output());
                INT_CODEC.encode(buf, recipe.time());
            }

            @Override
            public PressingRecipe decode(RegistryByteBuf buf) {
                Ingredient input = Ingredient.PACKET_CODEC.decode(buf);
                int ingredientCount = INT_CODEC.decode(buf);
                Ingredient container = Ingredient.PACKET_CODEC.decode(buf);
                ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
                int time = INT_CODEC.decode(buf);
                return new PressingRecipe(input, ingredientCount, container, output, time);
            }
        };

        @Override
        public MapCodec<PressingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, PressingRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

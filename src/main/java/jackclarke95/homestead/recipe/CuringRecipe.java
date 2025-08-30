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

public record CuringRecipe(
        Ingredient inputItem, // required
        int ingredientCount, // required, from JSON
        Ingredient catalyst, // optional, may be Ingredient.EMPTY
        Ingredient container, // optional, may be Ingredient.EMPTY
        ItemStack output, // required
        ItemStack byproduct, // optional, may be ItemStack.EMPTY
        int time // required
) implements Recipe<CuringRecipeInput> {

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.of();
        list.add(inputItem);
        if (!catalyst.isEmpty())
            list.add(catalyst);
        if (!container.isEmpty())
            list.add(container);
        return list;
    }

    @Override
    public boolean matches(CuringRecipeInput input, World world) {
        if (world.isClient()) {
            return false;
        }
        // Ingredient is always required
        if (!inputItem.test(input.getStackInSlot(0))) {
            return false;
        }
        // Catalyst is optional, but if present, must match
        if (!catalyst.isEmpty() && !catalyst.test(input.getStackInSlot(1))) {
            return false;
        }
        // Container is not required for matching, only for output transfer
        return true;
    }

    @Override
    public ItemStack craft(CuringRecipeInput input, WrapperLookup lookup) {
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

    public boolean hasCatalyst() {
        return !catalyst.isEmpty();
    }

    public boolean hasContainer() {
        return !container.isEmpty();
    }

    public boolean hasByproduct() {
        return !byproduct.isEmpty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CURING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CURING_TYPE;
    }

    public static class Serializer implements RecipeSerializer<CuringRecipe> {
        public static final MapCodec<CuringRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(CuringRecipe::inputItem),
                Codec.INT.optionalFieldOf("ingredientCount", 1).forGetter(CuringRecipe::ingredientCount),
                Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("catalyst", Ingredient.EMPTY)
                        .forGetter(r -> r.catalyst),
                Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("container", Ingredient.EMPTY)
                        .forGetter(r -> r.container),
                ItemStack.CODEC.fieldOf("result").forGetter(CuringRecipe::output),
                ItemStack.CODEC.optionalFieldOf("byproduct")
                        .forGetter(r -> (r.byproduct == null || r.byproduct.isEmpty()) ? java.util.Optional.empty()
                                : java.util.Optional.of(r.byproduct)),
                Codec.INT.fieldOf("time").forGetter(CuringRecipe::time))
                .apply(inst, (ingredient, ingredientCount, catalyst, container, result, byproductOpt,
                        time) -> new CuringRecipe(ingredient, ingredientCount, catalyst, container, result,
                                byproductOpt.orElse(ItemStack.EMPTY), time)));

        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());

        public static final PacketCodec<RegistryByteBuf, CuringRecipe> STREAM_CODEC = new PacketCodec<>() {
            @Override
            public void encode(RegistryByteBuf buf, CuringRecipe recipe) {
                Ingredient.PACKET_CODEC.encode(buf, recipe.inputItem());
                INT_CODEC.encode(buf, recipe.ingredientCount());
                Ingredient.PACKET_CODEC.encode(buf, recipe.catalyst());
                Ingredient.PACKET_CODEC.encode(buf, recipe.container());
                ItemStack.PACKET_CODEC.encode(buf, recipe.output());
                // Manual optional encoding for byproduct
                boolean hasByproduct = recipe.byproduct() != null && !recipe.byproduct().isEmpty();
                buf.writeBoolean(hasByproduct);
                if (hasByproduct) {
                    ItemStack.PACKET_CODEC.encode(buf, recipe.byproduct());
                }
                INT_CODEC.encode(buf, recipe.time());
            }

            @Override
            public CuringRecipe decode(RegistryByteBuf buf) {
                Ingredient input = Ingredient.PACKET_CODEC.decode(buf);
                int ingredientCount = INT_CODEC.decode(buf);
                Ingredient catalyst = Ingredient.PACKET_CODEC.decode(buf);
                Ingredient container = Ingredient.PACKET_CODEC.decode(buf);
                ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
                ItemStack byproduct = ItemStack.EMPTY;
                boolean hasByproduct = buf.readBoolean();
                if (hasByproduct) {
                    byproduct = ItemStack.PACKET_CODEC.decode(buf);
                }
                int time = INT_CODEC.decode(buf);
                return new CuringRecipe(input, ingredientCount, catalyst, container, output, byproduct, time);
            }
        };

        @Override
        public MapCodec<CuringRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CuringRecipe> packetCodec() {
            return STREAM_CODEC;
        }
    }
}

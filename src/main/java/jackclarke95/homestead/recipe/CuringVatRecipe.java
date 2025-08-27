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

public record CuringVatRecipe(
        Ingredient inputItem, // required
        Ingredient catalyst, // optional, may be Ingredient.EMPTY
        Ingredient container, // optional, may be Ingredient.EMPTY
        ItemStack output, // required
        ItemStack byproduct, // optional, may be ItemStack.EMPTY
        int time // required
) implements Recipe<CuringVatRecipeInput> {

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
    public boolean matches(CuringVatRecipeInput input, World world) {
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
        return ModRecipes.CURING_VAT_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CURING_VAT_TYPE;
    }

    public static class Serializer implements RecipeSerializer<CuringVatRecipe> {
        public static final MapCodec<CuringVatRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(CuringVatRecipe::inputItem),
                Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("catalyst", Ingredient.EMPTY)
                        .forGetter(r -> r.catalyst),
                Ingredient.DISALLOW_EMPTY_CODEC.optionalFieldOf("container", Ingredient.EMPTY)
                        .forGetter(r -> r.container),
                ItemStack.CODEC.fieldOf("result").forGetter(CuringVatRecipe::output),
                ItemStack.CODEC.optionalFieldOf("byproduct")
                        .forGetter(r -> (r.byproduct == null || r.byproduct.isEmpty()) ? java.util.Optional.empty()
                                : java.util.Optional.of(r.byproduct)),
                Codec.INT.fieldOf("time").forGetter(CuringVatRecipe::time))
                .apply(inst, (ingredient, catalyst, container, result, byproductOpt,
                        time) -> new CuringVatRecipe(ingredient, catalyst, container, result,
                                byproductOpt.orElse(ItemStack.EMPTY), time)));

        public static final PacketCodec<RegistryByteBuf, Integer> INT_CODEC = PacketCodec.of(
                (value, buf) -> buf.writeVarInt(value),
                buf -> buf.readVarInt());

        public static final PacketCodec<RegistryByteBuf, CuringVatRecipe> STREAM_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, CuringVatRecipe::inputItem,
                Ingredient.PACKET_CODEC, CuringVatRecipe::catalyst,
                Ingredient.PACKET_CODEC, CuringVatRecipe::container,
                ItemStack.PACKET_CODEC, CuringVatRecipe::output,
                // Manual optional encoding for byproduct
                new PacketCodec<RegistryByteBuf, ItemStack>() {
                    public void encode(RegistryByteBuf buf, ItemStack value) {
                        boolean present = value != null && !value.isEmpty();
                        buf.writeBoolean(present);
                        if (present) {
                            ItemStack.PACKET_CODEC.encode(buf, value);
                        }
                    }

                    public ItemStack decode(RegistryByteBuf buf) {
                        boolean present = buf.readBoolean();
                        if (present) {
                            return ItemStack.PACKET_CODEC.decode(buf);
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                },
                r -> r.byproduct,
                INT_CODEC, CuringVatRecipe::time,
                (input, catalyst, container, output, byproduct, time) -> new CuringVatRecipe(input, catalyst, container,
                        output, byproduct, time));

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

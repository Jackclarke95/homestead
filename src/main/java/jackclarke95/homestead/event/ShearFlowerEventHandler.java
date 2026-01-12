package jackclarke95.homestead.event;

import jackclarke95.homestead.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ShearFlowerEventHandler {
    private static final Map<Block, ItemStack> FLOWER_TO_SEED = new HashMap<>();
    static {
        FLOWER_TO_SEED.put(Blocks.ALLIUM, new ItemStack(ModItems.ALLIUM_SEEDS));
        FLOWER_TO_SEED.put(Blocks.AZURE_BLUET, new ItemStack(ModItems.AZURE_BLUET_SEEDS));
        FLOWER_TO_SEED.put(Blocks.BLUE_ORCHID, new ItemStack(ModItems.BLUE_ORCHID_SEEDS));
        FLOWER_TO_SEED.put(Blocks.CORNFLOWER, new ItemStack(ModItems.CORNFLOWER_SEEDS));
        FLOWER_TO_SEED.put(Blocks.DANDELION, new ItemStack(ModItems.DANDELION_SEEDS));
        FLOWER_TO_SEED.put(Blocks.LILY_OF_THE_VALLEY, new ItemStack(ModItems.LILY_OF_THE_VALLEY_SEEDS));
        FLOWER_TO_SEED.put(Blocks.ORANGE_TULIP, new ItemStack(ModItems.ORANGE_TULIP_SEEDS));
        FLOWER_TO_SEED.put(Blocks.OXEYE_DAISY, new ItemStack(ModItems.OXEYE_DAISY_SEEDS));
        FLOWER_TO_SEED.put(Blocks.PINK_TULIP, new ItemStack(ModItems.PINK_TULIP_SEEDS));
        FLOWER_TO_SEED.put(Blocks.POPPY, new ItemStack(ModItems.POPPY_SEEDS));
        FLOWER_TO_SEED.put(Blocks.RED_TULIP, new ItemStack(ModItems.RED_TULIP_SEEDS));
        FLOWER_TO_SEED.put(Blocks.WHITE_TULIP, new ItemStack(ModItems.WHITE_TULIP_SEEDS));
        FLOWER_TO_SEED.put(Blocks.TORCHFLOWER, new ItemStack(Items.TORCHFLOWER_SEEDS));
    }

    public static void register() {
        UseBlockCallback.EVENT.register(ShearFlowerEventHandler::onUseBlock);
    }

    // TODO: Drop seed *instead of* flower, and yield more flowers in the sewing bed

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient)
            return ActionResult.PASS;
        ItemStack stack = player.getStackInHand(hand);
        BlockPos pos = hitResult.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        if (stack.getItem() == Items.SHEARS && FLOWER_TO_SEED.containsKey(block)) {
            ItemStack flowerDrop = new ItemStack(block.asItem());
            Block.dropStack(world, pos, flowerDrop);

            int fortuneLevel = stack.getEnchantments()
                    .getEnchantmentEntries()
                    .stream()
                    .filter(x -> x.getKey().getIdAsString().equals("minecraft:fortune"))
                    .findFirst()
                    .map(x -> x.getIntValue())
                    .orElse(0);

            // 10% base chance to drop seeds, increased by 5% per fortune level
            int chance = 10 + (5 * fortuneLevel);
            if (new Random().nextInt(100) < chance) {
                Block.dropStack(world, pos, FLOWER_TO_SEED.get(block).copy());
            }

            // Remove the flower block without playing the break sound
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);

            world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);

            // Damage the shears
            stack.damage(1, player, LivingEntity.getSlotForHand(hand));

            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}

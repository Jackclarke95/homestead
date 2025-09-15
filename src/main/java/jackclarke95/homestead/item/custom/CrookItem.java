package jackclarke95.homestead.item.custom;

import jackclarke95.homestead.block.custom.FruitBearingLeaves;
import jackclarke95.homestead.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrookItem extends ToolItem {
    private static final double PULL_STRENGTH = 0.5;
    private static final double TOOL_RANGE_MODIFIER = 2.0;

    public CrookItem(ToolMaterial material, Settings settings) {
        super(material, settings.attributeModifiers(createAttributeModifiers(material)));
    }

    private static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material) {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                1.0 + material.getAttackDamage(), EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID,
                                -2.8, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                TOOL_RANGE_MODIFIER, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .build();
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            boolean isSneaking = user.isSneaking();

            Vec3d pullDirection;

            if (isSneaking) {
                pullDirection = entity.getPos().subtract(user.getPos()).normalize();
            } else {
                pullDirection = user.getPos().subtract(entity.getPos()).normalize();
            }

            Vec3d currentVelocity = entity.getVelocity();
            Vec3d newVelocity = pullDirection.multiply(PULL_STRENGTH);
            entity.setVelocity(currentVelocity.add(newVelocity));
            entity.velocityModified = true;

            if (isSneaking) {
                user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_FISHING_BOBBER_THROW,
                        SoundCategory.PLAYERS, 1.0F, 1.0F);
            } else {
                user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE,
                        SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            stack.damage(1, user, LivingEntity.getSlotForHand(hand));
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (player == null)
            return ActionResult.PASS;

        if (state.getBlock() instanceof FruitBearingLeaves &&
                state.contains(FruitBearingLeaves.FRUIT_STAGE)) {

            int fruitStage = state.get(FruitBearingLeaves.FRUIT_STAGE);

            if (fruitStage == 2) {
                if (!world.isClient) {
                    ((FruitBearingLeaves) state.getBlock()).setFruitStage(world, pos, 0);

                    dropFruitForBlock(state, world, pos);

                    world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,
                            SoundCategory.BLOCKS, 1.0F, 1.0F);

                    stack.damage(1, player, LivingEntity.getSlotForHand(context.getHand()));
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    private void dropFruitForBlock(BlockState state, World world, BlockPos pos) {
        if (!(world instanceof ServerWorld))
            return;

        String blockName = Registries.BLOCK.getId(state.getBlock()).getPath();
        ItemStack fruitToDrop = null;
        int dropCount = 2 + world.getRandom().nextInt(3);

        switch (blockName) {
            case "apple_tree_leaves":
                fruitToDrop = new ItemStack(Items.APPLE, dropCount);

                break;
            case "pear_tree_leaves":
                fruitToDrop = new ItemStack(ModItems.PEAR, dropCount);

                break;
            case "plum_tree_leaves":
                fruitToDrop = new ItemStack(ModItems.PLUM, dropCount);

                break;
            case "lemon_tree_leaves":
                fruitToDrop = new ItemStack(ModItems.LEMON, dropCount);

                break;
            case "orange_tree_leaves":
                fruitToDrop = new ItemStack(ModItems.ORANGE, dropCount);

                break;
            case "apricot_tree_leaves":
                fruitToDrop = new ItemStack(ModItems.APRICOT, dropCount);

                break;
            case "peach_tree_leaves":
                fruitToDrop = new ItemStack(ModItems.PEACH, dropCount);

                break;
        }

        if (fruitToDrop != null) {
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fruitToDrop);
        }
    }
}

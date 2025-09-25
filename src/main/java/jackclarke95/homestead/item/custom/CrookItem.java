package jackclarke95.homestead.item.custom;

import jackclarke95.homestead.block.ModBlocks;
import jackclarke95.homestead.block.custom.FruitBearingLeaves;
import jackclarke95.homestead.util.ModTags;
import jackclarke95.homestead.block.entity.custom.HamperBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

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

        boolean isFruitLeaves = state.getBlock() instanceof FruitBearingLeaves
                && state.contains(FruitBearingLeaves.FRUIT_STAGE);
        boolean isLeaves = state.getBlock().getTranslationKey().contains("leaves");
        boolean isSneaking = player.isSneaking();
        ItemStack offhand = player.getOffHandStack();
        boolean offhandPlaceable = !offhand.isEmpty() && offhand.getItem() instanceof BlockItem;

        if (isFruitLeaves) {
            if (isSneaking && offhandPlaceable) {
                // Sneaking with a block in offhand: always place, never harvest
                return ActionResult.PASS;
            } else if (!isSneaking) {
                // Not sneaking: always harvest, never place
                int fruitStage = state.get(FruitBearingLeaves.FRUIT_STAGE);
                if (fruitStage == 2) {
                    if (!world.isClient) {
                        List<ItemStack> fruitDrops = getFruitFromLootTable(state, world, pos, player, stack);
                        if (!fruitDrops.isEmpty()) {
                            ((FruitBearingLeaves) state.getBlock()).setFruitStage(world, pos, 0);
                            for (ItemStack fruit : fruitDrops) {
                                insertFruitOrDrop(player, fruit);
                            }
                            world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,
                                    SoundCategory.BLOCKS, 1.0F, 1.0F);
                            stack.damage(1, player, LivingEntity.getSlotForHand(context.getHand()));
                        }
                    }
                    return ActionResult.SUCCESS;
                }
                // Not ripe: do nothing, but never place a block
                return ActionResult.SUCCESS;
            } else {
                // Sneaking but no placeable block: do nothing, allow vanilla
                return ActionResult.PASS;
            }
        }

        // For all leaves blocks, prevent block placement unless sneaking with a
        // placeable block in offhand
        if (isLeaves) {
            if (isSneaking && offhandPlaceable) {
                return ActionResult.PASS;
            } else {
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS; // not leaves: vanilla behavior
    }

    // Attempts to insert fruit into mainhand/offhand hamper, then inventory, then
    // drops at feet
    private void insertFruitOrDrop(PlayerEntity player, ItemStack fruit) {
        if (fruit == null || fruit.isEmpty())
            return;

        // Try main hand hamper
        ItemStack mainHand = player.getMainHandStack();
        if (isHamper(mainHand) && HamperBlockEntity.isItemInFoodTags(fruit)) {
            int inserted = insertIntoHamper(mainHand, fruit);
            if (inserted > 0) {
                fruit.decrement(inserted);
                if (fruit.isEmpty())
                    return;
            }
        }

        // Try offhand hamper
        ItemStack offHand = player.getOffHandStack();
        if (isHamper(offHand) && HamperBlockEntity.isItemInFoodTags(fruit)) {
            int inserted = insertIntoHamper(offHand, fruit);
            if (inserted > 0) {
                fruit.decrement(inserted);
                if (fruit.isEmpty())
                    return;
            }
        }

        // Try player inventory
        if (player.getInventory().insertStack(fruit.copy())) {
            fruit.setCount(0);
            return;
        }

        // Drop at player's feet
        if (!player.getWorld().isClient && !fruit.isEmpty()) {
            ItemScatterer.spawn(player.getWorld(), player.getX(), player.getY(), player.getZ(), fruit.copy());
            fruit.setCount(0);
        }
    }

    private boolean isHamper(ItemStack stack) {
        return stack != null && !stack.isEmpty()
                && ModBlocks.HAMPER.asItem() == stack.getItem();
    }

    // Returns number of items inserted
    private int insertIntoHamper(ItemStack hamper, ItemStack toInsert) {
        int maxSlots = 27;
        DefaultedList<ItemStack> inventory = DefaultedList.ofSize(maxSlots, ItemStack.EMPTY);
        ContainerComponent container = hamper.get(DataComponentTypes.CONTAINER);
        if (container != null)
            container.copyTo(inventory);

        int toInsertCount = toInsert.getCount();
        int remaining = toInsertCount;
        int inserted = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slot = inventory.get(i);
            if (slot.isEmpty()) {
                int insertAmount = Math.min(remaining, toInsert.getMaxCount());
                ItemStack insertedStack = toInsert.copy();
                insertedStack.setCount(insertAmount);
                inventory.set(i, insertedStack);
                remaining -= insertAmount;
                inserted += insertAmount;
            } else if (ItemStack.areItemsAndComponentsEqual(slot, toInsert)
                    && slot.getCount() < slot.getMaxCount()) {
                int space = slot.getMaxCount() - slot.getCount();
                int insertAmount = Math.min(remaining, space);
                slot.increment(insertAmount);
                remaining -= insertAmount;
                inserted += insertAmount;
            }
            if (remaining <= 0)
                break;
        }
        hamper.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory));
        return inserted;
    }

    private List<ItemStack> getFruitFromLootTable(BlockState state, World world, BlockPos pos, PlayerEntity player,
            ItemStack tool) {
        if (!(world instanceof ServerWorld serverWorld))
            return List.of();

        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(serverWorld)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .add(LootContextParameters.TOOL, tool)
                .add(LootContextParameters.THIS_ENTITY, player)
                .add(LootContextParameters.BLOCK_STATE, state);

        var drops = state.getDroppedStacks(builder);

        // Filter and collect only fruit items
        return drops.stream()
                .filter(drop -> drop.isIn(ModTags.ItemTags.FRUITS))
                .toList();
    }
}

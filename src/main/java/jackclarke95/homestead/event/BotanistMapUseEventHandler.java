package jackclarke95.homestead.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.map.MapState;
import jackclarke95.homestead.villager.ModVillagers;

public class BotanistMapUseEventHandler {
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient && stack.getItem() == Items.FILLED_MAP
                    && stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (nbtComponent != null) {
                    net.minecraft.nbt.NbtCompound tag = nbtComponent.copyNbt();
                    if (tag != null && tag.contains("homestead_red_x")) {
                        String posString = tag.getString("homestead_red_x");
                        BlockPos pos = null;
                        try {
                            String[] parts = posString.split(",");
                            if (parts.length >= 2) {
                                int x = Integer.parseInt(parts[0].trim());
                                int z = Integer.parseInt(parts[1].trim());
                                pos = new BlockPos(x, 0, z);
                            }
                        } catch (Exception ignored) {
                        }
                        if (pos == null) {
                            MapIdComponent mapId = stack.get(DataComponentTypes.MAP_ID);
                            if (mapId != null) {
                                MapState mapState = ((ServerWorld) world).getMapState(mapId);
                                if (mapState != null) {
                                    pos = new BlockPos(mapState.centerX, 0, mapState.centerZ);
                                }
                            }
                        }
                        if (pos != null) {
                            ModVillagers.ensureRedXDecoration(stack, (ServerWorld) world, pos);
                        }
                    }
                }
            }
            return TypedActionResult.pass(stack);
        });
    }
}

package jackclarke95.homestead.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;

import jackclarke95.homestead.block.entity.custom.RackBlockEntity;

public class ModNetworking {

    public static void registerCommon() {
        PayloadTypeRegistry.playS2C().register(RackInventoryUpdatePacket.ID, RackInventoryUpdatePacket.CODEC);
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(RackInventoryUpdatePacket.ID, (packet, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world != null) {
                    // Try to get the block entity immediately
                    BlockEntity blockEntity = world.getBlockEntity(packet.pos());
                    if (blockEntity instanceof RackBlockEntity rackEntity) {
                        ItemStack stackToSet = packet.isEmpty() ? ItemStack.EMPTY : packet.stack();
                        // Force update the client-side inventory directly
                        rackEntity.setStackDirectly(0, stackToSet);
                    } else {
                        // Block entity might not be loaded yet, schedule a retry
                        MinecraftClient.getInstance().execute(() -> {
                            BlockEntity retryEntity = world.getBlockEntity(packet.pos());
                            if (retryEntity instanceof RackBlockEntity rackEntity) {
                                ItemStack stackToSet = packet.isEmpty() ? ItemStack.EMPTY : packet.stack();
                                rackEntity.setStackDirectly(0, stackToSet);
                            }
                        });
                    }
                }
            });
        });
    }
}

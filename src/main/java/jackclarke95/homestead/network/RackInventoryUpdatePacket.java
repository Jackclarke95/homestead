package jackclarke95.homestead.network;

import jackclarke95.homestead.Homestead;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record RackInventoryUpdatePacket(BlockPos pos, boolean isEmpty, ItemStack stack) implements CustomPayload {
    public static final CustomPayload.Id<RackInventoryUpdatePacket> ID = new CustomPayload.Id<>(
            Identifier.of(Homestead.MOD_ID, "rack_inventory_update"));

    public static final PacketCodec<RegistryByteBuf, RackInventoryUpdatePacket> CODEC = new PacketCodec<RegistryByteBuf, RackInventoryUpdatePacket>() {
        @Override
        public RackInventoryUpdatePacket decode(RegistryByteBuf buf) {
            BlockPos pos = BlockPos.PACKET_CODEC.decode(buf);
            boolean isEmpty = buf.readBoolean();
            ItemStack stack = isEmpty ? ItemStack.EMPTY : ItemStack.PACKET_CODEC.decode(buf);
            return new RackInventoryUpdatePacket(pos, isEmpty, stack);
        }

        @Override
        public void encode(RegistryByteBuf buf, RackInventoryUpdatePacket packet) {
            BlockPos.PACKET_CODEC.encode(buf, packet.pos());
            buf.writeBoolean(packet.isEmpty());
            if (!packet.isEmpty()) {
                // Additional safety check
                if (packet.stack().isEmpty()) {
                    ItemStack.PACKET_CODEC.encode(buf, new ItemStack(net.minecraft.item.Items.STONE));
                } else {
                    ItemStack.PACKET_CODEC.encode(buf, packet.stack());
                }
            }
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void send(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
        boolean isEmpty = stack.isEmpty();
        // Use a dummy valid stack if empty to avoid encoding issues
        ItemStack packetStack = isEmpty ? new ItemStack(net.minecraft.item.Items.STONE) : stack.copy();

        // Double-check: ensure consistency between isEmpty flag and actual stack
        if (isEmpty && !packetStack.isEmpty()) {
            ServerPlayNetworking.send(player, new RackInventoryUpdatePacket(pos, true, packetStack));
        } else if (!isEmpty && !stack.isEmpty()) {
            ServerPlayNetworking.send(player, new RackInventoryUpdatePacket(pos, false, packetStack));
        } else {
            // Force safe packet creation
            ServerPlayNetworking.send(player,
                    new RackInventoryUpdatePacket(pos, true, new ItemStack(net.minecraft.item.Items.STONE)));
        }
    }
}
